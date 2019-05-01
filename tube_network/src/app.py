# Copyright 2018 Grakn Labs Ltd
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import tkinter as tk
from grakn.client import GraknClient
import datetime


def transform_to_range(val, old_min, old_max, new_min, new_max):
    """
    Transform a value from an old range to a new range
    :return: scaled value
    """
    old_range = (old_max - old_min)
    new_range = (new_max - new_min)
    new_val = (((val - old_min) * new_range) / old_range) + new_min
    return new_val


def transform_coords(lon, lat, min_lon, max_lon, min_lat, max_lat, new_width, new_height):
    """
    Transforms grid coordinates to a coordinate system that can be easily rendered.
    :param lon: longitude of the coordinates to scale
    :param lat: latitude of the coordinates to scale
    :param min_lon: the minimum longitude, which will be mapped to x = 0
    :param max_lon: the maximum longitude, which will be mapped to x = new_width
    :param min_lat: the minimum latitude, which will be mapped to y = 0
    :param max_lat: the minimum latitude, which will be mapped to y = new_height
    :param new_width: the maximum height of the coordinates to map to
    :param new_height: the maximum width of the coordinates to map to
    :return:
    """
    lon = transform_to_range(lon, min_lon, max_lon, 0, new_width)
    lat = new_height - transform_to_range(lat, min_lat, max_lat, 0, new_height)
    return lon, lat


def _create_circle(self, x, y, r, **kwargs):
    """
    Helper function for easily drawing circles with tkinter, rather than ovals
    :param x: circle centre x-coordinate
    :param y: circle centre y-coordinate
    :param r: circle radius
    :param kwargs:
    :return:
    """
    return self.create_oval(x-r, y-r, x+r, y+r, **kwargs)


def execute_and_log(query, transaction):
    print("\n" + ";\n".join(query.split(";")).replace("match", "match\n"))
    response = transaction.query(query)
    print("... query complete.")
    return response


# Attach the circle helper function to tkinter so that we can use it more naturally
tk.Canvas.create_circle = _create_circle


class TubeGui:

    # Zoom attributes
    ZOOM_IN_SCALE = 2
    ZOOM_OUT_SCALE = 1/ZOOM_IN_SCALE

    # Size attributes
    RIVER_THAMES_WIDTH = 10
    STATION_FONT_SIZE = 12
    STATION_CIRCLE_RADIUS = 1
    STATION_K_CORE_MAX_RADIUS = 8
    STATION_DEGREE_MAX_RADIUS = 10
    ROUTES_DEGREE_MAX_RADIUS = 8
    TUNNEL_SHORTEST_PATH_WIDTH = 10
    # Station connections
    LINE_WIDTH = 2
    LINE_SPACING = 0.5

    # Color attributes
    RIVER_THAMES_COLOUR = "#def"
    STATION_K_CORE_COLOUR = "#AAF"
    STATION_DEGREE_COLOUR = "#FAA"
    ROUTES_DEGREE_COLOUR = "#AFA"
    TUNNEL_SHORTEST_PATH_COLOUR = "#DDD"

    # Hotkeys
    STATION_K_CORE_KEY = "k"
    STATION_DEGREE_KEY = "d"
    ROUTES_DEGREE_KEY = "r"
    CLEAR_SHORTEST_PATH_KEY = "q"
    CLEAR_ALL_KEY = "c"

    # Used in calculating aspect ratio
    COMPUTE_MIN_LAT = "compute min of lat, in station;"
    COMPUTE_MAX_LAT = "compute max of lat, in station;"
    COMPUTE_MIN_LON = "compute min of lon, in station;"
    COMPUTE_MAX_LON = "compute max of lon, in station;"

    COMPUTE_CENTRALITY_TUNNEL_DEGREE = "compute centrality of station, in [station, tunnel], using degree;"
    COMPUTE_CENTRALITY_TUNNEL_KCORE = "compute centrality of station, in [station, tunnel], using k-core;"
    COMPUTE_CENTRALITY_ROUTE_DEGREE = "compute centrality of station, in [station, route], using degree;"

    def __init__(self, session, root=tk.Tk()):
        """
            Main visualisation class. Builds an interactive map of the London tube.
            :param root:
            :param client:
        """
        start_time = datetime.datetime.now()

        self._root = root
        self._session = session
        self.w, self.h = self._root.winfo_screenwidth(), self._root.winfo_screenheight()
        self._root.geometry("%dx%d+0+0" % (self.w, self.h))
        self._root.focus_set()
        self._root.bind("<Escape>", lambda e: e.widget.quit())
        self._root.bind("<Key>", self._key_handler)
        self._root.title('London Tube Map')

        self._canvas = tk.Canvas(self._root)
        self._canvas.bind("<ButtonPress-1>", self._scan_start)
        self._canvas.bind("<ButtonRelease-1>", self._scan_stop)
        self._canvas.bind("<B1-Motion>", self._scan_move)
        self._canvas.pack(fill=tk.BOTH, expand=1)  # Stretch canvas to root window size.

        # We want to scale the longitude and lonitude to fit the image
        # To do this we need the minimum and maximum of the longitude and latitude,
        # we can query for this easily in Grakn!
        with session.transaction().read() as transaction:
            self.min_lat = list(execute_and_log(self.COMPUTE_MIN_LAT, transaction))[0].number()
            self.max_lat = list(execute_and_log(self.COMPUTE_MAX_LAT, transaction))[0].number()
            self.min_lon = list(execute_and_log(self.COMPUTE_MIN_LON, transaction))[0].number()
            self.max_lon = list(execute_and_log(self.COMPUTE_MAX_LON, transaction))[0].number()

        # aspect ratio as width over height, which is longitude over latitude
        aspect_ratio = (self.max_lon - self.min_lon) / (self.max_lat - self.min_lat)
        self.new_width = self.w
        self.new_height = self.new_width / aspect_ratio

        self._draw_river_thames()

        # We need to associate the id of the station entity in Grakn to the rendered dot on the screen, so that we can
        # find the Grakn id of a station that is clicked on
        self._station_point_ids = dict()
        # Also store the station coords so that we don't have to query Grakn for them again
        self._station_canvas_coords = dict()
        self._station_centrality_points = dict()

        self._draw()

        # self._draw_stations()

        # ===== Event state variables =====
        self._displaying_centrality = False
        self._scale = 1
        self._shortest_path_stations = []
        self._shortest_path_elements = []
        self._scan_delta = (0, 0)
        self._x_pos = 0
        self._y_pos = 0
        self._scanning = False

        end_time = datetime.datetime.now()
        print("- - - - - -\nTime taken: " + str(end_time - start_time))

    @staticmethod
    def get_visualisation_data(session):
        """
        Retrieve the data required for visualising the tube network
        :return: coordinates of all stations and their names
        """
        with session.transaction().read() as transaction:
            print("\nRetriving coordinates to draw stations and tunnels ...")
            answers_iterator = execute_and_log(
                'match' +
                '   $sta1 isa station, has lon $lon1, has lat $lat1, has name $sta1-nam;' +
                '   $sta2 isa station, has lon $lon2, has lat $lat2, has name $sta2-nam;' +
                '   $tun ($sta1, $sta2, service: $sec) isa tunnel, has identifier $tun-id;' +
                '   $tul isa tube-line, has name $tul-nam;' +
                '   (section: $sec, route-operator: $tul) isa route;' +
                'get $lon1, $lat1, $lon2, $lat2, $tun-id, $tul-nam, $sta1-nam, $sta2-nam, $sta1, $sta2;', transaction
            )

            coordinates = {}
            for answer in answers_iterator:
                answer = answer.map()
                tube_line_name = answer.get("tul-nam").value()
                tunnel_id = answer.get("tun-id").value()

                if tunnel_id in list(coordinates.keys()):
                    current_tube_lines = coordinates[tunnel_id]["tube-lines"]
                    if tube_line_name not in current_tube_lines:
                        current_tube_lines.append(tube_line_name)
                        updated_tube_lines = sorted(current_tube_lines)
                        coordinates[tunnel_id]["tube-lines"] = updated_tube_lines
                else:
                    lon1, lat1 = answer.get('lon1').value(), answer.get('lat1').value()
                    lon2, lat2 = answer.get('lon2').value(), answer.get('lat2').value()
                    coordinates[tunnel_id] = {
                        "tube-lines": [tube_line_name],
                        "from": {
                            "lon": lon1,
                            "lat": lat1,
                            "station_name": answer.get("sta1-nam").value()[:-len(" Underground Station")],
                            "station_id": answer.get("sta1").id

                        },
                        "to": {
                            "lon": lon2,
                            "lat": lat2,
                            "station_name": answer.get("sta2-nam").value()[:-len(" Underground Station")],
                            "station_id": answer.get("sta2").id
                        }
                    }

        return coordinates

    @staticmethod
    def find_shortest_path(session, ids):
        query = "compute path from " + ids[0] + ", to " + ids[1] + ", in [station, tunnel];"
        with session.transaction().read() as transaction:
            shortest_path_concept_list = list(execute_and_log(query, transaction))[0]

            # The response contains the different permutations for each path through stations. We are interested only in
            # which stations the path passes through
            shortest_path_ids = []
            for shortest_path_node_id in shortest_path_concept_list.list():
                concepts_list= list(transaction.query("match $sta id " + shortest_path_node_id + "; $sta has name $nam; get;"))
                if len(concepts_list) > 0:
                    concept = concepts_list[0]
                    if concept.map().get("sta").type().label() == 'station':
                        shortest_path_ids.append(shortest_path_node_id)

        return shortest_path_ids

    @staticmethod
    def compute_centrality(session, query):
        centrality_details = {
            "centrality_set": [],
            "max_score": 0
        }

        with session.transaction().read() as transaction:
            centralities = list(execute_and_log(query, transaction))
            # Find the max centrality value, that way we can scale the visualisation up to a maximum radius
            centrality_details["max_score"] = max([int(centrality.measurement()) for centrality in centralities])

            for centrality in centralities:
                centrality_set = {
                    "measurement": centrality.measurement(),
                    "concept_ids": []
                }

                for concept_id in centrality.set():
                    centrality_set["concept_ids"].append(concept_id)

                centrality_details["centrality_set"].append(centrality_set)

        print(centrality_details)

        return centrality_details

    def _transform_coords(self, lon, lat):
        """
        Transfrom grid coordinates to canvas coordinates
        :param lon: grid coordinate longitude
        :param lat: grid coordinate latitude
        :return: transformed coordination
        """

        return transform_coords(
            lon, lat, self.min_lon, self.max_lon, self.min_lat, self.max_lat, self.new_width, self.new_height
        )

    def _draw_river_thames(self):
        """
        draws a depiction of the River Thames, based on grid coordinates of the river's approximate centre-line.
        """
        # Grid coordinates of a path along the centre-line of the River Thames
        THAMES_WAYPOINTS = (
            (51.388592,-0.426814),(51.404487,-0.409858),(51.409538,-0.390457),(51.407749,-0.379153),(51.412011,-0.361944),(51.405223,-0.345663),(51.391487,-0.326768),
            (51.400803,-0.309137),(51.424900,-0.308209),(51.432526,-0.326262),(51.443253,-0.329383),(51.451718,-0.303738),(51.456028,-0.305088),(51.465068,-0.320778),(51.471164,-0.319176),
            (51.484088,-0.297243),(51.487082,-0.288217),(51.483983,-0.279444),(51.471742,-0.266622),(51.470839,-0.261951),(51.474607,-0.251973),(51.484884,-0.249098),(51.489681,-0.237854),
            (51.487997,-0.229271),(51.473779,-0.223284),(51.466401,-0.211568),(51.464329,-0.191527),(51.467697,-0.182686),(51.480180,-0.175627),(51.484764,-0.148011),(51.483788,-0.137282),
            (51.487129,-0.127519),(51.506112,-0.120438),(51.508943,-0.116210),(51.508916,-0.094474),(51.505297,-0.074797),(51.502198,-0.064648),(51.502131,-0.056944),(51.508155,-0.044370),
            (51.508035,-0.035337),(51.505244,-0.029843),(51.491952,-0.029285),(51.485566,-0.020659),(51.485272,-0.007892),(51.489935,-0.001047),(51.501490,-0.005360),(51.507260,0.001378),
            (51.506526,0.005648),(51.496922,0.021677),(51.497620,0.073815),(51.511549,0.090750),(51.516348,0.127855),(51.506580,0.167984),(51.503888,0.172763),(51.485042,0.184526),
            (51.485852,0.213678),(51.457240,0.280714)
        )

        scaled_thames_coords = []
        for lat, lon in THAMES_WAYPOINTS:
            lon, lat = self._transform_coords(lon, lat)
            scaled_thames_coords.append((lon, lat))

        self._canvas.create_line(
            *scaled_thames_coords,
            width=self.RIVER_THAMES_WIDTH,
            fill=self.RIVER_THAMES_COLOUR,
            joinstyle=tk.ROUND
        )

    def _draw(self):
        """
        Draws everything in the visualiser
        """
        print("\nDrawing ...")

        coordinates = self.get_visualisation_data(self._session)

        drawn_station_ids = []
        for tunnel_id, details in coordinates.items():
            TUBE_LINE_COLOURS = {
                "Bakerloo": "#B36305",
                "Central": "#E32017",
                "Circle": "#FFD300",
                "District": "#00782A",
                "Hammersmith & City": "#F3A9BB",
                "Jubilee": "#A0A5A9",
                "Metropolitan": "#9B0056",
                "Northern": "#000000",
                "Piccadilly": "#003688",
                "Victoria": "#0098D4",
                "Waterloo & City": "#95CDBA",
            }

            # Draw tunnels
            for i, tube_line_name in enumerate(details["tube-lines"]):
                # Trigonometry to draw parallel lines with consistent distance between them
                from_lon, from_lat = self._transform_coords(float(details["from"]["lon"]), float(details["from"]["lat"]))
                to_lon, to_lat = self._transform_coords(float(details["to"]["lon"]), float(details["to"]["lat"]))

                x = to_lon - from_lon
                y = to_lat - from_lat
                z = self.LINE_SPACING  # desired orthogonal displacement of parallel lines
                grad = y / x  # gradient of the connection to draw

                # The change in coordinates needed to achieve this
                y = ((grad ** 2 + 1) ** -0.5) * z
                x = grad * y

                self._canvas.create_line(
                    from_lon - (i * x),
                    from_lat + (i * y),
                    to_lon - (i * x),
                    to_lat + (i * y),
                    fill=TUBE_LINE_COLOURS[tube_line_name],
                    width=self.LINE_WIDTH
                )

            # Draw stations
            for station in [details["from"], details["to"]]:
                station_id = station["station_id"]
                if station_id not in drawn_station_ids: # draw each station only once
                    lon, lat = self._transform_coords(float(station["lon"]), float(station["lat"]))
                    # lon, lat = station["lon"], station["lat"]
                    stating_name = station["station_name"]

                    # Write label
                    station_label_tag = self._canvas.create_text(
                        lon + self.STATION_CIRCLE_RADIUS,
                        lat + self.STATION_CIRCLE_RADIUS,
                        text=stating_name,
                        anchor=tk.NW,
                        font=('Johnston', self.STATION_FONT_SIZE, 'bold'),
                        fill="#666"
                    )

                    # Draw circle
                    station_tag = self._canvas.create_circle(
                        lon,
                        lat,
                        self.STATION_CIRCLE_RADIUS,
                        fill="white",
                        outline="black"
                    )

                    self._station_canvas_coords[station_id] = (lon, lat)
                    self._station_point_ids[station_id] = station_tag

                    # station selection event handlers
                    def callback_wrapper(event, id=station_id): return self._on_station_select(id)
                    event_sequence = "<Shift-ButtonPress-1>"
                    self._canvas.tag_bind(station_tag, event_sequence, callback_wrapper)
                    self._canvas.tag_bind(station_label_tag, event_sequence, callback_wrapper)

                    drawn_station_ids.append(station_id)

        print("\nDone! you can now interact with the visualiser.")

    def _scan_start(self, event):
        """
            Processes the start of dragging with the mouse to pan
            :param event: event instance
        """
        self._canvas.scan_mark(event.x, event.y)
        self._scan_start_pos = event.x, event.y
        self._scanning = True

    def _scan_move(self, event):
        """
            Processes moving the mouse during dragging to pan
            :param event: event instance
        """
        self._canvas.scan_dragto(event.x, event.y, gain=1)
        self._scan_delta = event.x - self._scan_start_pos[0], event.y - self._scan_start_pos[1]

    def _scan_stop(self, event):
        """
            Processes the end of dragging with the mouse to pan
            :param event: event instance
        """
        self._x_pos += self._scan_delta[0]
        self._y_pos += self._scan_delta[1]
        self._scan_delta = (0, 0)
        self._scanning = False

    def _key_handler(self, event):
        """
            Handle a key press event, dispatching to the desired behaviour
            :param event: event instance, including the character that was pressed
        """
        if event.char == "+" or event.char == "=":
            self.zoom("in")

        if event.char == "-" or event.char == "_":
            self.zoom("out")

        if not self._displaying_centrality:
            if event.char == self.STATION_DEGREE_KEY:
                self.display_centrality(self.COMPUTE_CENTRALITY_TUNNEL_DEGREE, self.STATION_DEGREE_MAX_RADIUS, self.STATION_DEGREE_COLOUR)

            if event.char == self.STATION_K_CORE_KEY:
                self.display_centrality(self.COMPUTE_CENTRALITY_TUNNEL_KCORE, self.STATION_K_CORE_MAX_RADIUS, self.STATION_K_CORE_COLOUR)

            if event.char == self.ROUTES_DEGREE_KEY:
                self.display_centrality(self.COMPUTE_CENTRALITY_ROUTE_DEGREE, self.ROUTES_DEGREE_MAX_RADIUS, self.ROUTES_DEGREE_COLOUR)

        if event.char == self.CLEAR_SHORTEST_PATH_KEY:
                self.clear_shortest_path()

        if event.char == self.CLEAR_ALL_KEY:
            self.clear_all()

    def _on_station_select(self, station_id):
        """
        To be called when the user selects a station. Needs to be passed the unique Naptan-id of the station
        :param event:
        :param station_id:
        :return:
        """
        self._shortest_path_stations.append(station_id)

        x, y = self._get_station_point_coords(station_id)
        r = self._transform_to_current_scale(2 * self.STATION_CIRCLE_RADIUS)
        c = self._canvas.create_circle(x, y, r, fill=self.TUNNEL_SHORTEST_PATH_COLOUR, outline="")
        self._canvas.tag_lower(c, 1)

        self._shortest_path_elements.append(c)

        print(self._shortest_path_stations)

        if len(self._shortest_path_stations) > 1:
            shortest_path_ids = self.find_shortest_path(self._session, [self._shortest_path_stations[-2], self._shortest_path_stations[-1]])
            self.display_shortest_path(shortest_path_ids)

    def display_shortest_path(self, shortest_path_ids):
        """
        Renders the shortest path(s) from station to station
        :param shortest_path_ids: response from Grakn server
        """

        path_points = []
        for station_id in shortest_path_ids:
            # Add a point on the path for every station on the path
            x0, y0, x1, y1 = self._canvas.coords(self._station_point_ids[station_id])
            point = int((x0 + x1) / 2), int((y0 + y1) / 2)
            path_points.append(point)

        path = self._canvas.create_line(*path_points, width=self.TUNNEL_SHORTEST_PATH_WIDTH, fill=self.TUNNEL_SHORTEST_PATH_COLOUR, joinstyle=tk.ROUND, dash=(3, 3))
        self._shortest_path_elements.append(path)
        # Put the path behind the other visual elements on the map
        self._canvas.tag_lower(path, 1)

    def _get_station_point_coords(self, station_id):
        """
        Get the canvas coordinates of a station from its ID
        :param station_id: the ID of the desired station
        :return: the centre-point coordinates of the circle used to represent the station
        """
        x0, y0, x1, y1 = self._canvas.coords(self._station_point_ids[station_id])
        point = (x0 + x1) / 2, (y0 + y1) / 2
        return point

    def clear_shortest_path(self):
        """
        Delete from the canvas the elements being used to display shortest paths
        """
        self._canvas.delete(*self._shortest_path_elements)
        self._shortest_path_stations = []

    def clear_all(self):
        self.clear_shortest_path()
        self.hide_centrality()

    def zoom(self, direction):
        """
        "Zoom" the screen to magnify details. This entails scaling up the whole canvas, and some slightly complex
        translation of the canvas to give the effect of zooming in on the canvas point that sits at the centre of
        the window
        :param direction: "in" or "out", whether to magnify or de-magnify the map
        """
        if self._scanning:
            print("Currently scanning. Stop scanning to zoom.")
        else:
            if direction == "in":
                scaling = self.ZOOM_IN_SCALE
            elif direction == "out":
                scaling = self.ZOOM_OUT_SCALE
            else:
                raise ValueError("Call to zoom didn't specify a valid direction")

            # First, scale up the canvas about its origin. Doing this about the canvas origin keeps adding other
            # elements to the canvas simple, because then only scaling needs to be applied
            self._canvas.scale('all', 0, 0, scaling, scaling)

            # Update the persistent scale value
            self._scale *= scaling

            # Find the displacement to shift the canvas by, so that is appears to scale about the centre-point of the
            # window
            dx = -int((1 - scaling) * (self._x_pos - self.w / 2))
            dy = -int((1 - scaling) * (self._y_pos - self.h / 2))

            # Since we're shifting by this amount, also add this displacement to the persistent scan variables
            self._x_pos += dx
            self._y_pos += dy

            # Set an anchor to drag from. I believe this point is arbitrary
            self._canvas.scan_mark(0, 0)

            # The canvas is being scaled about its origin, so we only need to drag the delta to centre the scaling
            self._canvas.scan_dragto(dx, dy, gain=1)

    def _transform_to_current_scale(self, val):
        """
        Take a value, e.g. a coordinate, and scale it according to the current scaling of the canvas. This is mostly
        for the benefot of adding or removing rendered elements after the map has been zoomed
        :param val:
        :return:
        """
        return val * self._scale

    def display_centrality(self, query, upper_radius, colour):
        """
            Show an infographic-style visualisation of centrality, where the radius of the circles plotted corresponds to
            the centrality score
            :param query: graql centrality query as a string
            :param upper_radius:
            :param colour:
            :return:
        """

        centrality_details = self.compute_centrality(self._session, query)

        for centrality_set in centrality_details["centrality_set"]:
            radius = self._transform_to_current_scale(
                (int(int(centrality_set["measurement"])) / centrality_details["max_score"]) * upper_radius
            )

            for concept_id in centrality_set["concept_ids"]:
                print(concept_id, centrality_set["measurement"], centrality_details["max_score"])

                station_element_id = self._station_point_ids[concept_id]
                lon, lat = self._station_canvas_coords[concept_id]
                lon = self._transform_to_current_scale(lon)
                lat = self._transform_to_current_scale(lat)

                centrality_element_id = self._canvas.create_circle(lon, lat, radius, fill=colour, outline="")

                self._station_centrality_points[concept_id] = centrality_element_id

                # Send the drawn elements to behind the station point
                self._canvas.tag_lower(centrality_element_id, station_element_id)
        print(self._station_centrality_points)
        self._displaying_centrality = True

    def hide_centrality(self):
        if self._displaying_centrality:
            for concept_id, point_id in self._station_centrality_points.items():
                self._canvas.delete(point_id)
            self._displaying_centrality = False


if __name__ == "__main__":
    root = tk.Tk() # Build the Tkinter application
    with GraknClient(uri="localhost:48555") as client:
        with client.session(keyspace="tube_network") as session:
            tube_gui = TubeGui(session, root)
            root.mainloop()

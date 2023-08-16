package com.typedb.examples.cti.db;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typedb.examples.cti.model.domain.relationship.Sighting;

import java.util.List;

public class RelationDAO<T> {
    protected static final String REL_MATCH =
            "$ta (%s: $rp1, %s: $rp2) isa %s, has stix_id $id, has $attribute;" +
                    "$attribute isa! $j; ";
    private final String nameRel;
    private final TypeDBSessionWrapper db;
    private final List<String> typeString;
    private final List<String> rolePlayers;



    public RelationDAO(TypeDBSessionWrapper db, String nameRel, List<String> rolePlayers, List<String> typeString) {
        this.db = db;
        this.nameRel = nameRel;
        this.typeString = typeString;
        this.rolePlayers = rolePlayers;
    }

    private ObjectNode find(String getQueryStr) {
        return db.getRelJSON(getQueryStr, nameRel, rolePlayers);
    }

    public ObjectNode findAll() {
        String query = String.format(REL_MATCH, rolePlayers.get(0), rolePlayers.get(1), nameRel);
        var getQueryStr = "match " + query + "group $id; ";
        return find(getQueryStr);
    }

    public ObjectNode search(String attrType, String attrName) {

        if (typeString.contains(attrType)) {
            attrName = "\"" + attrName + "\"";
        }

        String query = String.format(REL_MATCH, rolePlayers.get(0), rolePlayers.get(1), nameRel);
        String search = "$ta has " + attrType + " = " + attrName + ";";
        var getQueryStr = "match " + query + search + "group $id;";

        return find(getQueryStr);
    }

}

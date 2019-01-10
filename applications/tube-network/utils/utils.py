
"""
Dedicated to getting the results you intend from your graql queries
"""


def check_response_length(response, min_length=None, max_length=None):
    """
    Throws runtime errors if the response doesn't return a number of elements within the prescribed range
    :param response:
    :param min_length:
    :param max_length:
    :return:
    """
    if (min_length is not None) and (len(response) < min_length):
        raise RuntimeError(("Grakn server response has length {}, but the minimum should have been {}. If using match, "
                            "insert, check that the \"match\" on it's own returns at least one combination of variables"
                            ".").format(len(response), min_length))
    elif (max_length is not None) and (len(response) > max_length):
        raise RuntimeError(("Grakn server response shows that {} insertions were made, but the maximum should have "
                            "been {}. If using match, insert, check that the \"match\" on it's own returns at the "
                            "desired number of combinations of variables.").format(len(response), max_length))
    elif min_length is None and max_length is None:
        raise RuntimeError("No bounds set on response length")

    elif min_length is not None and max_length is not None:
        if min_length > max_length:
            raise ValueError("Specified minimum length is greater than maximum length, which cannot be satisfied")


def _match(graql_body):
    prefix = "match "
    return prefix + graql_body


def match_get(graql_body):
    suffix = " get;"
    return _match(graql_body) + suffix


def insert(graql_body):
    prefix = "insert "
    return prefix + graql_body


def match_insert(match_graql_body, insert_graql_body):
    return _match(match_graql_body) + "\n" + insert(insert_graql_body)


def get_match_id(match_get_response, var_name):
    return match_get_response[0].get(var_name).id
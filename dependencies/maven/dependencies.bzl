# Do not edit. bazel-deps autogenerates this file from dependencies/maven/dependencies.yaml.
def _jar_artifact_impl(ctx):
    jar_name = "%s.jar" % ctx.name
    ctx.download(
        output=ctx.path("jar/%s" % jar_name),
        url=ctx.attr.urls,
        sha256=ctx.attr.sha256,
        executable=False
    )
    src_name="%s-sources.jar" % ctx.name
    srcjar_attr=""
    has_sources = len(ctx.attr.src_urls) != 0
    if has_sources:
        ctx.download(
            output=ctx.path("jar/%s" % src_name),
            url=ctx.attr.src_urls,
            sha256=ctx.attr.src_sha256,
            executable=False
        )
        srcjar_attr ='\n    srcjar = ":%s",' % src_name

    build_file_contents = """
package(default_visibility = ['//visibility:public'])
java_import(
    name = 'jar',
    tags = ['maven_coordinates={artifact}'],
    jars = ['{jar_name}'],{srcjar_attr}
)
filegroup(
    name = 'file',
    srcs = [
        '{jar_name}',
        '{src_name}'
    ],
    visibility = ['//visibility:public']
)\n""".format(artifact = ctx.attr.artifact, jar_name = jar_name, src_name = src_name, srcjar_attr = srcjar_attr)
    ctx.file(ctx.path("jar/BUILD"), build_file_contents, False)
    return None

jar_artifact = repository_rule(
    attrs = {
        "artifact": attr.string(mandatory = True),
        "sha256": attr.string(mandatory = True),
        "urls": attr.string_list(mandatory = True),
        "src_sha256": attr.string(mandatory = False, default=""),
        "src_urls": attr.string_list(mandatory = False, default=[]),
    },
    implementation = _jar_artifact_impl
)

def jar_artifact_callback(hash):
    src_urls = []
    src_sha256 = ""
    source=hash.get("source", None)
    if source != None:
        src_urls = [source["url"]]
        src_sha256 = source["sha256"]
    jar_artifact(
        artifact = hash["artifact"],
        name = hash["name"],
        urls = [hash["url"]],
        sha256 = hash["sha256"],
        src_urls = src_urls,
        src_sha256 = src_sha256
    )
    native.bind(name = hash["bind"], actual = hash["actual"])


def list_dependencies():
    return [
    {"artifact": "com.google.code.gson:gson:2.8.5", "lang": "java", "sha1": "f645ed69d595b24d4cf8b3fbb64cc505bede8829", "sha256": "233a0149fc365c9f6edbd683cfe266b19bdc773be98eabdaf6b3c924b48e7d81", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/com/google/code/gson/gson/2.8.5/gson-2.8.5.jar", "source": {"sha1": "c5b4c491aecb72e7c32a78da0b5c6b9cda8dee0f", "sha256": "512b4bf6927f4864acc419b8c5109c23361c30ed1f5798170248d33040de068e", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/com/google/code/gson/gson/2.8.5/gson-2.8.5-sources.jar"} , "name": "com-google-code-gson-gson", "actual": "@com-google-code-gson-gson//jar", "bind": "jar/com/google/code/gson/gson"},
    {"artifact": "com.univocity:univocity-parsers:2.8.1", "lang": "java", "sha1": "aa34369bc8766909a5633ce29eaba601e8e2e899", "sha256": "53e54caac234fd4e2cf5a3076ab0f047571d4e65c8bc94752c0ead267e9b2b71", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/com/univocity/univocity-parsers/2.8.1/univocity-parsers-2.8.1.jar", "source": {"sha1": "17f244c967d5a2086004ac27c2a369b4aec9da60", "sha256": "c7e74855217062c43c50bf3105d9bb6d7223bed9ecc099ff196d480d3e20530f", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/com/univocity/univocity-parsers/2.8.1/univocity-parsers-2.8.1-sources.jar"} , "name": "com-univocity-univocity-parsers", "actual": "@com-univocity-univocity-parsers//jar", "bind": "jar/com/univocity/univocity-parsers"},
    {"artifact": "commons-io:commons-io:2.6", "lang": "java", "sha1": "815893df5f31da2ece4040fe0a12fd44b577afaf", "sha256": "f877d304660ac2a142f3865badfc971dec7ed73c747c7f8d5d2f5139ca736513", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/commons-io/commons-io/2.6/commons-io-2.6.jar", "source": {"sha1": "2566800dc841d9d2c5a0d34d807e45d4107dbbdf", "sha256": "71bc251eb4bd011b60b5ce6adc8f473de10e4851207a40c14434604b288b31bf", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/commons-io/commons-io/2.6/commons-io-2.6-sources.jar"} , "name": "commons-io-commons-io", "actual": "@commons-io-commons-io//jar", "bind": "jar/commons-io/commons-io"},
    {"artifact": "javax.xml.stream:stax-api:1.0-2", "lang": "java", "sha1": "d6337b0de8b25e53e81b922352fbea9f9f57ba0b", "sha256": "e8c70ebd76f982c9582a82ef82cf6ce14a7d58a4a4dca5cb7b7fc988c80089b7", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/javax/xml/stream/stax-api/1.0-2/stax-api-1.0-2.jar", "source": {"sha1": "dd58a0151c110e27c6c18abd45b01792f46d3fcd", "sha256": "70b50265565dbbeb70ee3368c50e00220f8644da7a48bd67952c404e2bb0fd16", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/javax/xml/stream/stax-api/1.0-2/stax-api-1.0-2-sources.jar"} , "name": "javax-xml-stream-stax-api", "actual": "@javax-xml-stream-stax-api//jar", "bind": "jar/javax/xml/stream/stax-api"},
    {"artifact": "org.sharegov:mjson:1.3", "lang": "java", "sha1": "c50ea660b4a5dc35933f40afbb6a7e75ea47c12b", "sha256": "909b4e4dcf3342b884d279586f7520ed6423fa77feb22615a0e7f4e4466e6542", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/org/sharegov/mjson/1.3/mjson-1.3.jar", "source": {"sha1": "26938c8e2158144c5b5efa58117f9113d05d814f", "sha256": "8bfe367834a5ccc78becffe35366a2c3993a2ddb0b5a8f97663c97b883ccef32", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/org/sharegov/mjson/1.3/mjson-1.3-sources.jar"} , "name": "org-sharegov-mjson", "actual": "@org-sharegov-mjson//jar", "bind": "jar/org/sharegov/mjson"},
    ]

def maven_dependencies(callback = jar_artifact_callback):
    for hash in list_dependencies():
        callback(hash)
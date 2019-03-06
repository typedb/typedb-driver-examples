
java_test(
    name = "test-phone-calls-migration-xml-java",
    test_class = "grakn.example.TestPhoneCallsXMLMigration",
    srcs = [
        "TestPhoneCallsXMLMigration.java",
        "src/main/java/grakn/example/PhoneCallsXMLMigration.java"
    ],
    deps = [
        "@graknlabs_client_java//:client-java",
        "@graknlabs_graql//java:graql",
        "//dependencies/maven/artifacts/org/sharegov:mjson",
        "//dependencies/maven/artifacts/javax/xml/stream:stax-api"
    ],
    classpath_resources = [
        "@graknlabs_grakn_core//test-integration/resources:logback-test",
    ],
    data = [
        "//java:schema.gql",
        "files/phone-calls/data/calls.xml",
        "files/phone-calls/data/people.xml",
        "files/phone-calls/data/contracts.xml",
        "files/phone-calls/data/companies.xml"
    ]
)
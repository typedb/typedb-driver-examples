java_library(
	name = "migration-lib",
	srcs = ["Migration.java"],
	deps = [
		"@graknlabs_client_java//:client-java",
		"@graknlabs_graql//java:graql",
		"//dependencies/maven/artifacts/org/sharegov:mjson",
		"//dependencies/maven/artifacts/com/univocity:univocity-parsers"
	],
	data = [
		"//datasets:xcom-data"
	]
)

java_library(
	name = "queries-lib",
	srcs = ["Queries.java"],
	deps = [
		"@graknlabs_client_java//:client-java",
		"@graknlabs_graql//java:graql",
	]
)

java_test(
	name = "test",
	test_class = "grakn.example.xcom.XCOMTest",
	srcs = [
		"XCOMTest.java"
	],
	deps = [
		"@graknlabs_client_java//:client-java",
		"@graknlabs_graql//java:graql",
		":migration-lib",
		":queries-lib"
	],
	classpath_resources = [
		"@graknlabs_grakn_core//test-integration/resources:logback-test",
	],
	data = [
		"//schemas:xcom-schema.gql"
	]
)

java_binary(
	name = "migration",
	main_class = "grakn.example.xcom.Migration",
	srcs = ["Migration.java"],
	deps = [
		"@graknlabs_client_java//:client-java",
		"@graknlabs_graql//java:graql",
		"//dependencies/maven/artifacts/org/sharegov:mjson",
		"//dependencies/maven/artifacts/com/univocity:univocity-parsers"
	],
	classpath_resources = [
		"@graknlabs_grakn_core//test-integration/resources:logback-test",
	],
	data = [
		"//datasets:xcom-data"
	]
)

java_binary(
	name = "queries",
	main_class = "grakn.example.xcom.Queries",
	srcs = ["Queries.java"],
	deps = [
		"@graknlabs_client_java//:client-java",
		"@graknlabs_graql//java:graql",
	],
	classpath_resources = [
		"@graknlabs_grakn_core//test-integration/resources:logback-test",
	]
)

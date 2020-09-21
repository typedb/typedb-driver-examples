java_library(
	name = "migration-lib",
	srcs = ["Migration.java"],
	deps = [
		"@graknlabs_client_java//:client-java",
		"@graknlabs_graql//java:graql",
		"@maven//:org_sharegov_mjson",
        "@maven//:com_univocity_univocity_parsers"
	],
	data = [
		"//datasets:xcom-data"
	]
)

java_library(
	name = "queries-lib",
	srcs = ["Queries.java", "TransactionMode.java", "ResearchTask.java", "InventoryItem.java", "Result.java"],
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
	data = [
		"//schemas:xcom-schema.gql"
	],
    resources = ["logback-test.xml"],
    resource_strip_prefix = "xcom",
    size = "large",
)

java_binary(
	name = "migration",
	main_class = "grakn.example.xcom.Migration",
	srcs = ["Migration.java"],
	deps = [
		"@graknlabs_client_java//:client-java",
		"@graknlabs_graql//java:graql",
		"@maven//:org_sharegov_mjson",
        "@maven//:com_univocity_univocity_parsers"
	],
	data = [
		"//datasets:xcom-data"
	]
)

java_binary(
	name = "queries",
	main_class = "grakn.example.xcom.Queries",
	srcs = ["Queries.java", "TransactionMode.java", "ResearchTask.java", "InventoryItem.java", "Result.java"],
	deps = [
		"@graknlabs_client_java//:client-java",
		"@graknlabs_graql//java:graql",
	],
)

package junit.json;


interface JSONTests {

	void testLoadFromJSON_allItemsThere_shouldBeRead();

	void testLoadFromJSON_someItemsAreMissing_ShouldFail();

	void testPushToJSON_shouldBeSameAsLoaded();

	void testPushToJSON_someThingAltered_shouldBeDifferent();

	void testPushToJSON_reload_shouldBeRead();

}
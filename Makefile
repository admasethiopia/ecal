APP_JAR := build/ecal-clean.jar
MAIN_CLASS := org.ecal.EthiopicaCalendricaApp
SOURCES := $(shell find src/main/java -name '*.java' | sort)
TEST_SOURCES := $(shell find src/test/java -name '*.java' 2>/dev/null | sort)
ICU4J_JAR := tools/icu4j-77.1.jar
FLATLAF_VERSION := 3.4.1
FLATLAF_JAR := tools/flatlaf-$(FLATLAF_VERSION).jar
FLATLAF_URL := https://repo1.maven.org/maven2/com/formdev/flatlaf/$(FLATLAF_VERSION)/flatlaf-$(FLATLAF_VERSION).jar

.PHONY: all clean run test

all: $(APP_JAR)

$(FLATLAF_JAR):
	mkdir -p tools
	curl -fsSL -o $(FLATLAF_JAR) $(FLATLAF_URL)

# The runnable jar bundles the FlatLaf classes so it stays a single self-contained jar.
$(APP_JAR): $(SOURCES) src/main/resources/NotoSansEthiopic.ttf $(FLATLAF_JAR)
	rm -rf build/clean-classes
	mkdir -p build/clean-classes
	javac -cp $(FLATLAF_JAR) -d build/clean-classes $(SOURCES)
	cp -R src/main/resources/. build/clean-classes/
	cd build/clean-classes && jar -xf ../../$(FLATLAF_JAR)
	rm -rf build/clean-classes/META-INF build/clean-classes/module-info.class
	jar --create --file $(APP_JAR) --main-class $(MAIN_CLASS) -C build/clean-classes .

run: $(APP_JAR)
	java -jar $(APP_JAR)

test: $(APP_JAR) $(TEST_SOURCES)
	mkdir -p build/test-classes
	if [ -f "$(ICU4J_JAR)" ]; then \
		javac -cp build/clean-classes:$(ICU4J_JAR) -d build/test-classes $(TEST_SOURCES); \
		java -cp build/clean-classes:build/test-classes:$(ICU4J_JAR) org.ecal.TestSuite; \
	else \
		javac -cp build/clean-classes -d build/test-classes $(TEST_SOURCES); \
		java -cp build/clean-classes:build/test-classes org.ecal.TestSuite; \
	fi

clean:
	rm -rf build/clean-classes build/test-classes $(APP_JAR)

APP_JAR := build/ecal-clean.jar
MAIN_CLASS := org.ecal.EthiopicaCalendricaApp
SOURCES := $(shell find src/main/java -name '*.java' | sort)
TEST_SOURCES := $(shell find src/test/java -name '*.java' 2>/dev/null | sort)
ICU4J_JAR := tools/icu4j-77.1.jar

.PHONY: all clean run test

all: $(APP_JAR)

$(APP_JAR): $(SOURCES) src/main/resources/NotoSansEthiopic.ttf
	mkdir -p build/clean-classes
	javac -d build/clean-classes $(SOURCES)
	cp -R src/main/resources/. build/clean-classes/
	jar --create --file $(APP_JAR) --main-class $(MAIN_CLASS) -C build/clean-classes .

run: $(APP_JAR)
	java -jar $(APP_JAR)

test: $(APP_JAR) $(TEST_SOURCES)
	mkdir -p build/test-classes
	if [ -f "$(ICU4J_JAR)" ]; then \
		javac -cp build/clean-classes:$(ICU4J_JAR) -d build/test-classes $(TEST_SOURCES); \
		java -cp build/clean-classes:build/test-classes:$(ICU4J_JAR) org.ecal.CalendarConversionCheck; \
	else \
		javac -cp build/clean-classes -d build/test-classes $(TEST_SOURCES); \
		java -cp build/clean-classes:build/test-classes org.ecal.CalendarConversionCheck; \
	fi

clean:
	rm -rf build/clean-classes build/test-classes $(APP_JAR)

package kz.greetgo.scheduling.trigger;

public class TriggerParser {

  public static TriggerParseResult parse(String triggerString) {

    TriggerParserStructuring tokenizer = new TriggerParserStructuring(triggerString);
    tokenizer.makeStruct();

    throw new RuntimeException("not impl");
  }

}

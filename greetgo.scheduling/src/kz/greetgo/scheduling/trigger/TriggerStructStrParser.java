package kz.greetgo.scheduling.trigger;

import kz.greetgo.scheduling.trigger.TriggerStructStrLexer.Lex;
import kz.greetgo.scheduling.util.TriggerUtil;

import java.util.ArrayList;
import java.util.List;

import static kz.greetgo.scheduling.trigger.TriggerStructStrLexer.LexType.AFTER_PAUSE;
import static kz.greetgo.scheduling.trigger.TriggerStructStrLexer.LexType.FROM;
import static kz.greetgo.scheduling.trigger.TriggerStructStrLexer.LexType.REPEAT;
import static kz.greetgo.scheduling.trigger.TriggerStructStrLexer.LexType.TIME_OF_DAY;
import static kz.greetgo.scheduling.trigger.TriggerStructStrLexer.LexType.TIME_VALUE;
import static kz.greetgo.scheduling.trigger.TriggerStructStrLexer.LexType.TO;

public class TriggerStructStrParser {

  private final Range range;
  private final String source;

  private TriggerStructStrParser(Range range, String source) {
    this.range = range;
    this.source = source;
  }

  public static TriggerStructStrParser of(Range range, String source) {
    return new TriggerStructStrParser(range, source);
  }

  public final List<ParseError> errorList = new ArrayList<>();

  final List<Trigger> or1 = new ArrayList<>();
  final List<Trigger> or2 = new ArrayList<>();

  public Trigger parse() {

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(range, source);

    lexer.parse();

    if (lexer.errorList.size() > 0) {
      errorList.addAll(lexer.errorList);
      return null;
    }

    parseLexList(lexer.lexList);

    if (lexer.errorList.size() > 0) {
      return null;
    }

    return collectTriggers();
  }


  private Trigger collectTriggers() {

    if (or1.isEmpty() && or2.isEmpty()) {
      errorList.add(new ParseError(range, "5b654b7", "Нет расписания"));
      return null;
    }

    if (or1.isEmpty()) {
      return TriggerUtil.orList(or2);
    }
    if (or2.isEmpty()) {
      return TriggerUtil.orList(or1);
    }

    {
      Trigger o1 = TriggerUtil.orList(or1);
      Trigger o2 = TriggerUtil.orList(or2);

      return TriggerUtil.and(o1, o2);
    }

  }

  private void parseLexList(List<Lex> lexList) {

    if (lexList.isEmpty()) {
      errorList.add(new ParseError(range, "n1w2mk5", "Нет расписания"));
      return;
    }

    int i = 0, len = lexList.size();

    while (i < len) {

      Lex lex = lexList.get(i);

      if (lex.type == REPEAT) {

        int step = 2;

        if (i + 1 >= len) {
          errorList.add(new ParseError(lex.range(), "2n4b6v7", "Не указано количество повторений"));
          return;
        }

        Lex next = lexList.get(i + 1);

        if (next.type != TIME_VALUE) {
          errorList.add(new ParseError(lex.range(), "2h355h4", "Нужно указать величину повторений"));
          return;
        }

        if (or1.size() > 0) {
          errorList.add(new ParseError(lex.range(), "b54n254", "Повторения можно указывать только один раз"));
          return;
        }

        long delayMillis = next.readTimeValueMillis();

        long startSilentMillis = 0;

        if (i + 2 < len) {

          Lex next2 = lexList.get(i + 2);

          if (next2.type == AFTER_PAUSE) {

            if (i + 3 >= len) {
              errorList.add(new ParseError(next2.range(), "b54n254", "Не указано время паузы"));
              return;
            }

            Lex next3 = lexList.get(i + 3);

            if (next3.type != TIME_VALUE) {
              errorList.add(new ParseError(lex.range(), "j3u5b6v", "Нужно указать величину паузы"));
              return;
            }

            step = 4;
            startSilentMillis = next3.readTimeValueMillis();

          }

        }

        or1.add(new TriggerRepeat(startSilentMillis, delayMillis));

        i += step;
        continue;

      }

      if (lex.type == FROM) {
        if (i + 1 >= len) {
          errorList.add(new ParseError(lex.range(), "nsy3u8w", "Не указано количество повторений"));
          return;
        }

        Lex fromValue = lexList.get(i + 1);

        if (fromValue.type != TIME_OF_DAY) {
          errorList.add(new ParseError(lex.range(), "n2u63h2", "Несогласованная лексема - ожидается время HH:mm[:ss]"));
          return;
        }

        if (i + 2 >= len) {
          errorList.add(new ParseError(lex.range(), "iqE72WW", "Не указано конечное время"));
          return;
        }

        Lex to = lexList.get(i + 2);

        if (to.type != TO) {
          errorList.add(new ParseError(lex.range(), "ws7iq92", "Несогласованная лексема - ожидается" +
            " указатель окончания временного интервала"));
          return;
        }

        if (i + 3 >= len) {
          errorList.add(new ParseError(lex.range(), "qii543w", "Незаконченая лексема - ожидается время HH:mm[:ss]"));
          return;
        }

        Lex toValue = lexList.get(i + 3);

        if (toValue.type != TIME_OF_DAY) {
          errorList.add(new ParseError(lex.range(), "qiu2777", "Несогласованная лексема - ожидается время HH:mm[:ss]"));
          return;
        }

        long fromMillis = fromValue.readTimeOfDayInMillis();
        long toMillis = toValue.readTimeOfDayInMillis();

        Trigger trigger = new TriggerPeriodInDay(fromMillis, toMillis);

        or2.add(trigger);

      }

      errorList.add(new ParseError(lex.range(), "j25bhj4", "Несогласованная лексема"));
      return;
    }

  }

}

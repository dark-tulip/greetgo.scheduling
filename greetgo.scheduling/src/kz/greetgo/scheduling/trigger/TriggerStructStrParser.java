package kz.greetgo.scheduling.trigger;

import kz.greetgo.scheduling.trigger.TriggerStructStrLexer.Lex;
import kz.greetgo.scheduling.trigger.atoms.TriggerDayPoint;
import kz.greetgo.scheduling.trigger.atoms.TriggerPeriodInDay;
import kz.greetgo.scheduling.trigger.atoms.TriggerPeriodInDayRepeat;
import kz.greetgo.scheduling.trigger.atoms.TriggerRepeat;
import kz.greetgo.scheduling.trigger.atoms.TriggerWeekDay;

import java.util.ArrayList;
import java.util.List;

import static kz.greetgo.scheduling.trigger.TriggerStructStrLexer.LexType.AFTER_PAUSE;
import static kz.greetgo.scheduling.trigger.TriggerStructStrLexer.LexType.EVERY;
import static kz.greetgo.scheduling.trigger.TriggerStructStrLexer.LexType.FROM;
import static kz.greetgo.scheduling.trigger.TriggerStructStrLexer.LexType.REPEAT;
import static kz.greetgo.scheduling.trigger.TriggerStructStrLexer.LexType.TIME_OF_DAY;
import static kz.greetgo.scheduling.trigger.TriggerStructStrLexer.LexType.TIME_VALUE;
import static kz.greetgo.scheduling.trigger.TriggerStructStrLexer.LexType.TO;
import static kz.greetgo.scheduling.trigger.TriggerStructStrLexer.LexType.WEEK_DAY;

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

  Trigger trigger = null;

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

    if (trigger == null) {
      errorList.add(new ParseError(Range.of(0, source.length()), "214hY88", "Триггер не определён"));
      return null;
    }

    return trigger;
  }


  private void parseLexList(List<Lex> lexList) {

    if (lexList.isEmpty()) {
      errorList.add(new ParseError(range, "n1w2mk5", "Нет расписания"));
      return;
    }

    int i = 0, len = lexList.size();

    while (i < len) {

      Lex lex = lexList.get(i);

      if (trigger != null) {

        if (trigger instanceof TriggerPeriodInDay && lex.type == EVERY) {

          if (i + 1 >= len) {
            errorList.add(new ParseError(lex.range(), "2135jh6", "Не указана частота повторений"));
            return;
          }

          Lex next = lexList.get(i);

          if (next.type != TIME_VALUE) {
            errorList.add(new ParseError(lex.range(), "1b4ydWQ", "Не та лексема - нужно указать частоту повторений"));
            return;
          }

          trigger = new TriggerPeriodInDayRepeat((TriggerPeriodInDay)trigger, next.readTimeValueMillis());

        }

        errorList.add(new ParseError(lex.range(), "2h4hY88", "Лишняя лексема - триггер уже определён"));
        return;
      }

      if (lex.type == REPEAT) {

        int step = 2;

        if (i + 1 >= len) {
          errorList.add(new ParseError(lex.range(), "2n4b6v7", "Не указано количество повторений"));
          return;
        }

        Lex next = lexList.get(i + 1);

        if (next.type != TIME_VALUE) {
          errorList.add(new ParseError(lex.range(), "2h355h4", "Нужно указать частоту повторений"));
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

        trigger = new TriggerRepeat(startSilentMillis, delayMillis);

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

        trigger = new TriggerPeriodInDay(fromMillis, toMillis);

        i += 4;
        continue;

      }

      if (lex.type == WEEK_DAY) {

        trigger = new TriggerWeekDay(lex.getWeekDay());

        i++;
        continue;
      }

      if (lex.type == TIME_OF_DAY) {

        trigger = new TriggerDayPoint(lex.tokens.get(0).str());

        i++;
        continue;
      }

      errorList.add(new ParseError(lex.range(), "j25bhj4", "Несогласованная лексема"));
      return;
    }

  }


}

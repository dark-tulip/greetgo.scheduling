package kz.greetgo.scheduling.trigger;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class TriggerParserStructuring {

  private final String triggerString;

  public TriggerParserStructuring(String triggerString) {
    this.triggerString = triggerString;
  }

  enum TokenType {
    STR, PLUS, MUL, OPEN_BRACKET, CLOSE_BRACKET,
  }

  class Token {
    final TokenType type;
    final Range range;

    public Token(int from, int to, TokenType type) {
      requireNonNull(type);
      this.type = type;
      range = new Range(from, to);
    }

    public String source() {
      return range.cut(triggerString);
    }

    @Override
    public String toString() {
      return source();
    }
  }

  public final List<Token> tokens = new ArrayList<>();

  public void makeTokens() {

    int prevIndex = 0;

    for (int index = 0; index < triggerString.length(); index++) {

      char c = triggerString.charAt(index);

      if (c == '*' || c == '+') {

        if (prevIndex != index) {
          if (triggerString.substring(prevIndex, index).trim().length() > 0) {
            tokens.add(new Token(prevIndex, index, TokenType.STR));
          }
        }

        tokens.add(new Token(index, index + 1, c == '*' ? TokenType.MUL : TokenType.PLUS));
        prevIndex = index + 1;

        continue;
      }

      if (c == '(' || c == ')') {

        if (prevIndex != index) {

          if (triggerString.substring(prevIndex, index).trim().length() > 0) {
            tokens.add(new Token(prevIndex, index, TokenType.STR));
          }

        }

        tokens.add(new Token(index, index + 1, c == '(' ? TokenType.OPEN_BRACKET : TokenType.CLOSE_BRACKET));
        prevIndex = index + 1;

      }

    }

    if (triggerString.substring(prevIndex).trim().length() > 0) {
      tokens.add(new Token(prevIndex, triggerString.length(), TokenType.STR));
    }

  }

  public TriggerStruct result;

  int tokenIndex = 0;

  public void makeStruct() {
    tokenIndex = 0;
    result = readStruct(null);
  }

  class Operation implements ExpressionElement {
    final Token token;

    public Operation(Token token) {
      this.token = token;
    }

    @Override
    public Range range() {
      return token.range;
    }
  }

  public final List<ParseError> parseErrors = new ArrayList<>();

  private TriggerStruct readStruct(Token openedBracket) {
    List<ExpressionElement> elements = new ArrayList<>();

    while (true) {
      Token token = null;
      if (tokenIndex < tokens.size()) {
        token = tokens.get(tokenIndex++);
      }

      if (token == null) {
        return constructStruct(elements);
      }

      switch (token.type) {

        case CLOSE_BRACKET: {
          if (openedBracket == null) {
            parseErrors.add(new ParseError(token.range, "h56b4v6", "Несогласованная закрывающая скобка"));
          }
          return constructStruct(elements);
        }

        case OPEN_BRACKET:
          elements.add(readStruct(token));
          continue;

        case PLUS:
        case MUL:
          elements.add(new Operation(token));
          continue;

        case STR:
          elements.add(new TriggerStructStr(token.source(), token.range));
          continue;

        default:
          throw new RuntimeException("Unknown token.type = " + token.type);

      }
    }
  }

  private TriggerStruct constructStruct(List<ExpressionElement> elements) {

    if (elements.isEmpty()) {
      parseErrors.add(new ParseError(new Range(0, 0), "6h5bv47", "Нет данных"));
      return new TriggerStructEmpty();
    }

    TriggerStruct first;

    {
      ExpressionElement firstElement = elements.remove(0);
      if (firstElement instanceof Operation) {
        parseErrors.add(new ParseError(firstElement.range(), "6h56bv3", "Нет данных"));
        return new TriggerStructEmpty();
      }
      first = (TriggerStruct) firstElement;
    }

    class Pair {
      final Operation operation;
      final TriggerStruct operand;

      public Pair(Operation operation, TriggerStruct operand) {
        this.operation = operation;
        this.operand = operand;
      }
    }

    List<Pair> pairs = new ArrayList<>();

    Operation pre = null;
    for (ExpressionElement element : elements) {
      if (element instanceof Operation) {
        if (pre != null) {
          parseErrors.add(new ParseError(element.range(), "h4gh3vf", "Повторная операция"));
          return new TriggerStructEmpty();
        }
        pre = (Operation) element;
        continue;
      }

      TriggerStruct triggerStruct = (TriggerStruct) element;
      if (pre == null) {
        parseErrors.add(new ParseError(triggerStruct.range(), "5hb6267", "Отсутствует операция впереди"));
        return new TriggerStructEmpty();
      }
      pairs.add(new Pair(pre, triggerStruct));
      pre = null;
    }

    if (pre != null) {
      parseErrors.add(new ParseError(pre.range(), "33j5nb6", "Несогласованная операция"));
      return new TriggerStructEmpty();
    }


    TriggerStruct second = null;

    for (Pair pair : pairs) {

      switch (pair.operation.token.type) {

        case PLUS:
          if (second == null) {
            second = pair.operand;
          } else {
            first = new TriggerStructPlus(first, second);
            second = pair.operand;
          }
          continue;

        case MUL:
          if (second == null) {
            first = new TriggerStructMul(first, pair.operand);
          } else {
            second = new TriggerStructMul(second, pair.operand);
          }
          continue;

        default:
          throw new RuntimeException("Недопустимый тип : pair.operation.token.type = " + pair.operation.token.type);
      }

    }

    if (second != null) {
      first = new TriggerStructPlus(first, second);
    }

    return first;
  }

}

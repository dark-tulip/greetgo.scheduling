package kz.greetgo.scheduling.trigger.inner_logic;

import org.testng.annotations.Test;

import static kz.greetgo.scheduling.trigger.inner_logic.TriggerParserStructuring.TokenType.CLOSE_BRACKET;
import static kz.greetgo.scheduling.trigger.inner_logic.TriggerParserStructuring.TokenType.MUL;
import static kz.greetgo.scheduling.trigger.inner_logic.TriggerParserStructuring.TokenType.OPEN_BRACKET;
import static kz.greetgo.scheduling.trigger.inner_logic.TriggerParserStructuring.TokenType.PLUS;
import static kz.greetgo.scheduling.trigger.inner_logic.TriggerParserStructuring.TokenType.STR;
import static org.assertj.core.api.Assertions.assertThat;

public class TriggerParserStructuringTest {

  @Test
  public void makeStruct_plus_mul() {
    String source = " asd 123  fds + asd 1-0 * x1 ";
    TriggerParserStructuring structuring = TriggerParserStructuring.of(Range.of(0, source.length()), source);

    structuring.makeTokens();

    //
    //
    structuring.makeStruct();
    //
    //

    TriggerStruct struct = structuring.triggerStruct;

    assertThat(struct).isNotNull();
    assertThat(struct).isInstanceOf(TriggerStructPlus.class);
    TriggerStructPlus plus1 = (TriggerStructPlus) struct;

    assertThat(plus1.a).isInstanceOf(TriggerStructStr.class);

    {
      TriggerStructStr a = (TriggerStructStr) plus1.a;
      assertThat(a.source()).isEqualTo("asd 123  fds");
    }

    assertThat(plus1.b).isInstanceOf(TriggerStructMul.class);
    TriggerStructMul mul = (TriggerStructMul) plus1.b;

    assertThat(mul.a).isInstanceOf(TriggerStructStr.class);

    {
      TriggerStructStr a = (TriggerStructStr) mul.a;
      assertThat(a.source()).isEqualTo("asd 1-0");
    }
    {
      TriggerStructStr b = (TriggerStructStr) mul.b;
      assertThat(b.source()).isEqualTo("x1");
    }

  }


  @Test
  public void makeTokens() {
    String source = " asd 123  fds + asd 1-0 * (x1 + 77 11 ddd) * --11 1g2 ";
    TriggerParserStructuring structuring = TriggerParserStructuring.of(Range.of(0, source.length()), source);

    //
    //
    structuring.makeTokens();
    //
    //

    assertThat(structuring.tokens.get(0).toString()).isEqualTo(" asd 123  fds ");
    assertThat(structuring.tokens.get(0).type).isEqualTo(STR);
    assertThat(structuring.tokens.get(1).toString()).isEqualTo("+");
    assertThat(structuring.tokens.get(1).type).isEqualTo(PLUS);
    assertThat(structuring.tokens.get(2).toString()).isEqualTo(" asd 1-0 ");
    assertThat(structuring.tokens.get(2).type).isEqualTo(STR);
    assertThat(structuring.tokens.get(3).toString()).isEqualTo("*");
    assertThat(structuring.tokens.get(3).type).isEqualTo(MUL);
    assertThat(structuring.tokens.get(4).toString()).isEqualTo("(");
    assertThat(structuring.tokens.get(4).type).isEqualTo(OPEN_BRACKET);
    assertThat(structuring.tokens.get(5).toString()).isEqualTo("x1 ");
    assertThat(structuring.tokens.get(5).type).isEqualTo(STR);
    assertThat(structuring.tokens.get(6).toString()).isEqualTo("+");
    assertThat(structuring.tokens.get(6).type).isEqualTo(PLUS);
    assertThat(structuring.tokens.get(7).toString()).isEqualTo(" 77 11 ddd");
    assertThat(structuring.tokens.get(7).type).isEqualTo(STR);
    assertThat(structuring.tokens.get(8).toString()).isEqualTo(")");
    assertThat(structuring.tokens.get(8).type).isEqualTo(CLOSE_BRACKET);
    assertThat(structuring.tokens.get(9).toString()).isEqualTo("*");
    assertThat(structuring.tokens.get(9).type).isEqualTo(MUL);
    assertThat(structuring.tokens.get(10).toString()).isEqualTo(" --11 1g2 ");
    assertThat(structuring.tokens.get(10).type).isEqualTo(STR);
    assertThat(structuring.tokens).hasSize(11);


  }

  @Test
  public void makeStruct_error1() {
    String source = " a + b * (c + d) sss ";
    TriggerParserStructuring structuring = TriggerParserStructuring.of(Range.of(0, source.length()), source);

    //
    //
    structuring.makeTokens();
    structuring.makeStruct();
    //
    //

    for (ParseError parseError : structuring.parseErrors) {
      System.err.println("5gv4326gf :: " + parseError);
    }

    assertThat(structuring.parseErrors).hasSize(1);
    assertThat(structuring.parseErrors.get(0).errorCode).isEqualTo("5hb6267");
    assertThat(structuring.parseErrors.get(0).range).isEqualTo(Range.of(16, 21));
  }


  @Test
  public void makeStruct_error2() {
    String source = " a + b * (c + d) ) ";
    TriggerParserStructuring structuring = TriggerParserStructuring.of(Range.of(0, source.length()), source);

    //
    //
    structuring.makeTokens();
    structuring.makeStruct();
    //
    //

    for (ParseError parseError : structuring.parseErrors) {
      System.err.println("5gv4326gf :: " + parseError);
    }

    assertThat(structuring.parseErrors).hasSize(1);
    assertThat(structuring.parseErrors.get(0).errorCode).isEqualTo("h56b4v6");
    assertThat(structuring.parseErrors.get(0).range).isEqualTo(Range.of(17, 18));

  }

  @Test
  public void makeStruct_plus_mul_bracket_plus() {
    String source = " a + b * (c + d) ";
    TriggerParserStructuring structuring = TriggerParserStructuring.of(Range.of(0, source.length()), source);

    structuring.makeTokens();

    //
    //
    structuring.makeStruct();
    //
    //

    assertThat(structuring.parseErrors).isEmpty();

    TriggerStruct result = structuring.triggerStruct;

    System.out.println("vb543n26v :: result = " + result);

    assertThat(result).isNotNull();
    assertThat(result).isInstanceOf(TriggerStructPlus.class);
    TriggerStructPlus plus1 = (TriggerStructPlus) result;

    {
      assertThat(plus1.a).isInstanceOf(TriggerStructStr.class);
      TriggerStructStr a = (TriggerStructStr) plus1.a;
      assertThat(a.source()).isEqualTo("a");
    }

    assertThat(plus1.b).isInstanceOf(TriggerStructMul.class);
    TriggerStructMul mul = (TriggerStructMul) plus1.b;


    {
      assertThat(mul.a).isInstanceOf(TriggerStructStr.class);
      TriggerStructStr a = (TriggerStructStr) mul.a;
      assertThat(a.source()).isEqualTo("b");
    }
    {
      assertThat(mul.b).isInstanceOf(TriggerStructPlus.class);
      TriggerStructPlus last = (TriggerStructPlus) mul.b;

      {
        assertThat(last.a).isInstanceOf(TriggerStructStr.class);
        assertThat(((TriggerStructStr) last.a).source()).isEqualTo("c");
      }
      {
        assertThat(last.b).isInstanceOf(TriggerStructStr.class);
        assertThat(((TriggerStructStr) last.b).source()).isEqualTo("d");
      }

    }

  }


}

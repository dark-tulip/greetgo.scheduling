package kz.greetgo.scheduling.trigger;

import org.testng.annotations.Test;

import static kz.greetgo.scheduling.trigger.TriggerParserStructuring.TokenType.CLOSE_BRACKET;
import static kz.greetgo.scheduling.trigger.TriggerParserStructuring.TokenType.MUL;
import static kz.greetgo.scheduling.trigger.TriggerParserStructuring.TokenType.OPEN_BRACKET;
import static kz.greetgo.scheduling.trigger.TriggerParserStructuring.TokenType.PLUS;
import static kz.greetgo.scheduling.trigger.TriggerParserStructuring.TokenType.STR;
import static org.fest.assertions.api.Assertions.assertThat;

public class TriggerParserStructuringTest {

  @Test
  public void makeStruct_plus_mul() {
    TriggerParserStructuring structuring = new TriggerParserStructuring(
      " asd 123  fds + asd 1-0 * x1 "
    );

    structuring.makeTokens();

    //
    //
    structuring.makeStruct();
    //
    //

    TriggerStruct struct = structuring.result;

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
    TriggerParserStructuring structuring = new TriggerParserStructuring(
      " asd 123  fds + asd 1-0 * (x1 + 77 11 ddd) * --11 1g2 "
    );

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
  public void makeStruct_plus_mul_bracket_plus() {
    TriggerParserStructuring structuring = new TriggerParserStructuring(
      " a + b * (c + d) "
    );

    structuring.makeTokens();

    //
    //
    structuring.makeStruct();
    //
    //

    TriggerStruct result = structuring.result;

    System.out.println("result = " + result);

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

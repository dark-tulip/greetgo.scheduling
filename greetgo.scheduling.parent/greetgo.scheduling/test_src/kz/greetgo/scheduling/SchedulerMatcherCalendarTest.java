package kz.greetgo.scheduling;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class SchedulerMatcherCalendarTest {

  @DataProvider
  public Object[][] toIntDayOfWeek_data() {
    return new Object[][]{

      new Object[]{"Воскресенье", 1},
      new Object[]{"Понедельник", 2},
      new Object[]{"Вторник", 3},
      new Object[]{"Среда", 4},
      new Object[]{"Четверг", 5},
      new Object[]{"Пятница", 6},
      new Object[]{"Суббота", 7},

      new Object[]{"Вс", 1},
      new Object[]{"Пн", 2},
      new Object[]{"Вт", 3},
      new Object[]{"Ср", 4},
      new Object[]{"Чт", 5},
      new Object[]{"Пт", 6},
      new Object[]{"Сб", 7},

      new Object[]{"Вос", 1},
      new Object[]{"Пон", 2},
      new Object[]{"Вто", 3},
      new Object[]{"Сре", 4},
      new Object[]{"Чет", 5},
      new Object[]{"Пят", 6},
      new Object[]{"Суб", 7},

      new Object[]{"Sunday", 1},
      new Object[]{"Monday", 2},
      new Object[]{"Tuesday", 3},
      new Object[]{"Wednesday", 4},
      new Object[]{"Thursday", 5},
      new Object[]{"Friday", 6},
      new Object[]{"Saturday", 7},

      new Object[]{"Sun", 1},
      new Object[]{"Mon", 2},
      new Object[]{"Tue", 3},
      new Object[]{"Wed", 4},
      new Object[]{"Thu", 5},
      new Object[]{"Fri", 6},
      new Object[]{"Sat", 7},

    };
  }

  @Test(dataProvider = "toIntDayOfWeek_data")
  public void dayOfWeekToInt(String dayName, int expectedValue) throws Exception {

    //
    //
    final int actualValue = SchedulerMatcherCalendar.dayOfWeekToInt(dayName, "test pattern", "test place");
    //
    //

    assertThat(actualValue).isEqualTo(expectedValue);
  }

  @DataProvider
  public Object[][] monthToInt_data() {
    return new Object[][]{
      new Object[]{"Январь", 1},
      new Object[]{"Февраль", 2},
      new Object[]{"Март", 3},
      new Object[]{"Апрель", 4},
      new Object[]{"Май", 5},
      new Object[]{"Июнь", 6},
      new Object[]{"Июль", 7},
      new Object[]{"Август", 8},
      new Object[]{"Сентабрь", 9},
      new Object[]{"Октябрь", 10},
      new Object[]{"Ноябрь", 11},
      new Object[]{"Декабрь", 12},
      new Object[]{"January", 1},
      new Object[]{"February", 2},
      new Object[]{"March", 3},
      new Object[]{"April", 4},
      new Object[]{"May", 5},
      new Object[]{"June", 6},
      new Object[]{"July", 7},
      new Object[]{"AugUst", 8},
      new Object[]{"September", 9},
      new Object[]{"October", 10},
      new Object[]{"November", 11},
      new Object[]{"December", 12},

      new Object[]{"Янв", 1},
      new Object[]{"Фев", 2},
      new Object[]{"Мар", 3},
      new Object[]{"Апр", 4},
      new Object[]{"Май", 5},
      new Object[]{"Июн", 6},
      new Object[]{"Июл", 7},
      new Object[]{"АвГ", 8},
      new Object[]{"Сен", 9},
      new Object[]{"Окт", 10},
      new Object[]{"Ноя", 11},
      new Object[]{"Дек", 12},
      new Object[]{"Jan", 1},
      new Object[]{"Feb", 2},
      new Object[]{"MaR", 3},
      new Object[]{"Apr", 4},
      new Object[]{"May", 5},
      new Object[]{"Jun", 6},
      new Object[]{"Jul", 7},
      new Object[]{"Aug", 8},
      new Object[]{"Sep", 9},
      new Object[]{"Oct", 10},
      new Object[]{"Nov", 11},
      new Object[]{"Dec", 12},

    };
  }

  @Test(dataProvider = "monthToInt_data")
  public void monthToInt(String dayName, int expectedValue) throws Exception {

    //
    //
    final int actual = SchedulerMatcherCalendar.monthToInt(dayName, "test pattern", "test place");
    //
    //

    assertThat(actual).isEqualTo(expectedValue);
  }
}
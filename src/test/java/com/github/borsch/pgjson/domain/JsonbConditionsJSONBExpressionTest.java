package com.github.borsch.pgjson.domain;

import com.github.borsch.pgjson.postgresql.domain.jsonquery.implementation.JsonIteratorMode;
import com.github.borsch.pgjson.postgresql.domain.jsonquery.implementation.JsonProperty;
import com.github.borsch.pgjson.postgresql.domain.jsonquery.implementation.conditions.BetweenJsonCondition;
import com.github.borsch.pgjson.postgresql.domain.jsonquery.implementation.conditions.InJsonCondition;
import com.github.borsch.pgjson.postgresql.domain.jsonquery.implementation.conditions.NullJsonCondition;
import com.github.borsch.pgjson.postgresql.domain.jsonquery.implementation.conditions.SimpleJsonCondition;
import com.github.borsch.pgjson.postgresql.domain.jsonquery.model.ParametrizedValue;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;

public class JsonbConditionsJSONBExpressionTest {

	@Test
	public void testSimpleConditionEquals() {
		SimpleJsonCondition condition = new SimpleJsonCondition(new JsonProperty("data", "one.two.three[0]"), new ParametrizedValue("value"), "=");
		assertEquals("data#>>'{one,two,three,0}'=value", condition.toSqlString());

		condition = new SimpleJsonCondition(new JsonProperty("data", "one.two.three[0]")
				.mode(JsonIteratorMode.ARROW_ITERATOR_MODE),
				new ParametrizedValue("value"), "=");
		assertEquals("data->'one'->'two'->'three'->>0=value", condition.toSqlString());
	}

	@Test
	public void testSimpleConditionIgnoreValues() {
		SimpleJsonCondition condition = new SimpleJsonCondition(
				new JsonProperty("data", "one.two.three[0]"),
				new ParametrizedValue("value"),
				"=").ignoreValues();
		assertEquals("data#>>'{one,two,three,0}'=?", condition.toSqlString());

		condition = new SimpleJsonCondition(
				new JsonProperty("data", "one.two.three[0]").mode(JsonIteratorMode.ARROW_ITERATOR_MODE),
				new ParametrizedValue("value"),
				"=").ignoreValues();
		assertEquals("data->'one'->'two'->'three'->>0=?", condition.toSqlString());
	}

	@Test
	public void testSimpleConditionIgnoreValuesWithCustomKey() {
		SimpleJsonCondition condition = new SimpleJsonCondition(
				new JsonProperty("data", "one.two.three[0]"),
				new ParametrizedValue("key", "value"),
				"=").ignoreValues();
		assertEquals("data#>>'{one,two,three,0}'=:key", condition.toSqlString());

		condition = new SimpleJsonCondition(
				new JsonProperty("data", "one.two.three[0]").mode(JsonIteratorMode.ARROW_ITERATOR_MODE),
				new ParametrizedValue("key", "value"),
				"=").ignoreValues();
		assertEquals("data->'one'->'two'->'three'->>0=:key", condition.toSqlString());
	}

	@Test
	public void testSimpleConditionLowercase() {
		SimpleJsonCondition condition = new SimpleJsonCondition(
				new JsonProperty("data", "one.two.three[0]"),
				new ParametrizedValue("value"),
				"=").ignoreCase();
		assertEquals("lower(data#>>'{one,two,three,0}')=value", condition.toSqlString());

		condition = new SimpleJsonCondition(
				new JsonProperty("data", "one.two.three[0]").mode(JsonIteratorMode.ARROW_ITERATOR_MODE),
				new ParametrizedValue("value"),
				"=").ignoreCase();
		assertEquals("lower(data->'one'->'two'->'three'->>0)=value", condition.toSqlString());
	}

	@Test
	public void testNullCondition() {
		NullJsonCondition condition = new NullJsonCondition(
				new JsonProperty("data", "one.two.three[0]"), true);
		assertEquals("data#>>'{one,two,three,0}' IS NULL ", condition.toSqlString());

		condition = new NullJsonCondition(
				new JsonProperty("data", "one.two.three[0]").mode(JsonIteratorMode.ARROW_ITERATOR_MODE), true);
		assertEquals("data->'one'->'two'->'three'->>0 IS NULL ", condition.toSqlString());
	}

	@Test
	public void testNotNullCondition() {
		NullJsonCondition condition = new NullJsonCondition(
				new JsonProperty("data", "one.two.three[0]"), false);
		assertEquals("data#>>'{one,two,three,0}' IS NOT NULL ", condition.toSqlString());

		condition = new NullJsonCondition(
				new JsonProperty("data", "one.two.three[0]").mode(JsonIteratorMode.ARROW_ITERATOR_MODE), false);
		assertEquals("data->'one'->'two'->'three'->>0 IS NOT NULL ", condition.toSqlString());
	}

	@Test
	public void testBetweenCondition() {
		BetweenJsonCondition condition = new BetweenJsonCondition(
				new JsonProperty("data", "one.two.three[0]"), new ParametrizedValue(10), new ParametrizedValue(20));
		assertEquals("data#>>'{one,two,three,0}' BETWEEN 10 AND 20", condition.toSqlString());

		condition = new BetweenJsonCondition(
				new JsonProperty("data", "one.two.three[0]").mode(JsonIteratorMode.ARROW_ITERATOR_MODE),
				new ParametrizedValue(10), new ParametrizedValue(20));
		assertEquals("data->'one'->'two'->'three'->>0 BETWEEN 10 AND 20", condition.toSqlString());
	}

	@Test
	public void testNotBetweenCondition() {
		BetweenJsonCondition condition = new BetweenJsonCondition(
				new JsonProperty("data", "one.two.three[0]"), new ParametrizedValue(10), new ParametrizedValue(20)).not();
		assertEquals("data#>>'{one,two,three,0}' NOT BETWEEN 10 AND 20", condition.toSqlString());

		condition = new BetweenJsonCondition(
				new JsonProperty("data", "one.two.three[0]").mode(JsonIteratorMode.ARROW_ITERATOR_MODE),
				new ParametrizedValue(10), new ParametrizedValue(20)).not();
		assertEquals("data->'one'->'two'->'three'->>0 NOT BETWEEN 10 AND 20", condition.toSqlString());
	}

	@Test
	public void testInCondition() {
		InJsonCondition condition = new InJsonCondition(
				new JsonProperty("data", "one.two.three[0]"), Arrays.asList(new ParametrizedValue(10), new ParametrizedValue(20)));
		assertEquals("data#>>'{one,two,three,0}' IN ( 10,20 ) ", condition.toSqlString());

		condition = new InJsonCondition(
				new JsonProperty("data", "one.two.three[0]").mode(JsonIteratorMode.ARROW_ITERATOR_MODE),
				Arrays.asList(new ParametrizedValue(10), new ParametrizedValue(20)));
		assertEquals("data->'one'->'two'->'three'->>0 IN ( 10,20 ) ", condition.toSqlString());
	}

	@Test
	public void testNotInCondition() {
		InJsonCondition condition = new InJsonCondition(
				new JsonProperty("data", "one.two.three[0]"), Arrays.asList(new ParametrizedValue(10), new ParametrizedValue(20))).not();
		assertEquals("data#>>'{one,two,three,0}' NOT IN ( 10,20 ) ", condition.toSqlString());

		condition = new InJsonCondition(
				new JsonProperty("data", "one.two.three[0]").mode(JsonIteratorMode.ARROW_ITERATOR_MODE),
				Arrays.asList(new ParametrizedValue(10), new ParametrizedValue(20))).not();
		assertEquals("data->'one'->'two'->'three'->>0 NOT IN ( 10,20 ) ", condition.toSqlString());
	}
}

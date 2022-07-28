package com.yteplyi.sandbox.reactive.payment_service.utils;

import io.vertx.core.cli.converters.Converter;
import io.vertx.core.json.JsonArray;
import io.vertx.sqlclient.Row;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.BiFunction;

public class RowReturningMapper {

    public static JsonArray map(Row row) {
        JsonArray result = new JsonArray();

        String rowResult = row.getString(0);

        String values = sanitizeRow(rowResult);

        StringTokenizer tokenizer = new StringTokenizer(values, ",");

        while (tokenizer.hasMoreTokens()) {
            addValue(result, tokenizer.nextToken());
        }

        return result;
    }

    private static String sanitizeRow(String row) {
        return row.replace("(", "")
                .replace(")", "")
                .replace("\"", "");
    }

    private static void addValue(JsonArray array, String value) {
        ValueParserContext parserContext = new ValueParserContext();
        Object parsedValue = parserContext.parse(value);
        array.add(parsedValue);
    }

    private static class LocalDateTimeValueParser implements ValueParser {

        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        @Override
        public boolean handle(String value, ValueParserContext ctx) {
            try {
                LocalDateTime parse = LocalDateTime.parse(value, formatter);
                ctx.setResult(parse);
                return true;
            } catch (DateTimeException ex) {
                return ctx.next().handle(value, ctx);
            }
        }
    }

    private static class NumberValueParser implements ValueParser {

        @Override
        public boolean handle(String value, ValueParserContext ctx) {
            BiFunction<String, Converter<Number>, Boolean> parseFn = (v, p) -> {
                try {
                    Number number = p.fromString(v);
                    ctx.setResult(number);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            };
            List<Converter<Number>> numberParsers = Arrays.asList(
                    Integer::valueOf,
                    Long::valueOf,
                    Float::valueOf,
                    Double::valueOf
            );
            for (Converter<Number> numberParser : numberParsers) {
                Boolean parsed = parseFn.apply(value, numberParser);
                if (parsed) {
                    return true;
                }
            }
            return ctx.next().handle(value, ctx);
        }

    }

    private static class StringValueParser implements ValueParser {

        @Override
        public boolean handle(String value, ValueParserContext ctx) {
            ctx.setResult(value);
            return true;
        }

    }

    private static class ValueParserContext {

        private final List<ValueParser> parsers;

        private int index = 0;

        private Object result;

        public ValueParserContext() {
            this.parsers = new ArrayList<>();
            this.parsers.add(new NumberValueParser());
            this.parsers.add(new LocalDateTimeValueParser());
            this.parsers.add(new StringValueParser());
        }

        public Object parse(String value) {
            ValueParser parser = parsers.get(index++);
            boolean handled = parser.handle(value, this);
            if (handled) {
                return this.result;
            } else {
                throw new IllegalStateException("Value was not parsed");
            }
        }

        public ValueParser next() {
            if (this.index < this.parsers.size()) {
                return this.parsers.get(this.index++);
            } else {
                throw new IllegalStateException("Value was not parsed");
            }
        }

        public void setResult(Object value) {
            this.result = value;
        }

    }

    private interface ValueParser {

        boolean handle(String value, ValueParserContext ctx);

    }

}

package com.meutudo.bank.enums;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

@SuppressWarnings("serial")
public class CustomEnumDeserializer<T extends IBaseEnum<T>> extends StdDeserializer<T> {
	
	private final Class<T> targetClass;
	
	public CustomEnumDeserializer(Class<T> clazz) {
		super(clazz);
		this.targetClass = clazz;
	}

	public T deserialize(JsonParser jsonParser, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        boolean ehTipoPrimitivo = node.findValue("codigo") == null;

        return this.getEnum(node, ehTipoPrimitivo);
    }
	
	private T getEnum(JsonNode node, boolean ehTipoPrimitivo) {
        Integer idEnumJson = ehTipoPrimitivo ? node.intValue() : node.findValue("codigo").asInt();
        List<Object> enumValues = Arrays.asList(this.targetClass.getEnumConstants());

        for(int i = 0; i < enumValues.size(); ++i) {
            Object enumType = enumValues.get(i);
            Class<?> clzzEnum = enumType.getClass();
            Method method = null;
            Integer idEnum = null;

            try {
                method = clzzEnum.getMethod("getCodigo");
            } catch (NoSuchMethodException var12) {
                var12.printStackTrace();
            } catch (SecurityException var13) {
                var13.printStackTrace();
            }

            try {
                idEnum = (Integer)method.invoke(enumType);
            } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var11) {
                var11.printStackTrace();
            }

            if (idEnumJson.equals(idEnum)) {
                return (T) enumType;
            }
        }

        return null;
    }

}
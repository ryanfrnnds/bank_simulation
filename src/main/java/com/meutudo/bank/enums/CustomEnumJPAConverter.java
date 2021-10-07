package com.meutudo.bank.enums;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.persistence.AttributeConverter;


public abstract class CustomEnumJPAConverter<T extends Enum<T> & IBaseEnum<T>, Integer>
		implements AttributeConverter<T, Integer> {
	private final Class<T> targetClass;
	
	public CustomEnumJPAConverter(Class<T> clazz) {
		this.targetClass = clazz;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Integer convertToDatabaseColumn(T attribute) {
		
		Method method = null;
		Integer idEnum = null;
		try {
			method = attribute.getClass().getDeclaredMethod("getCodigo");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		try {
			idEnum = (Integer) method.invoke(attribute);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}

		return idEnum;
	}

	@Override
	public T convertToEntityAttribute(Integer dbData) {
		List<Object> enumValues = Arrays.asList(targetClass.getEnumConstants());
		for (int i = 0; i < enumValues.size(); i++) {
			Object enumType = enumValues.get(i);
			Class<?> clzzEnum = enumType.getClass();
			Method method = null;
			Integer idEnum = null;
			try {
				method = clzzEnum.getDeclaredMethod("getCodigo");
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
			try {
				idEnum = (Integer) method.invoke(enumType);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}

			if (dbData.equals(idEnum)) {
				return (T) enumType;
			}
		}

		throw new UnsupportedOperationException();
	}

}

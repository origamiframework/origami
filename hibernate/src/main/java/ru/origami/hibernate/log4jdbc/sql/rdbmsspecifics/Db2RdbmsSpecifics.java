package ru.origami.hibernate.log4jdbc.sql.rdbmsspecifics;

import ru.origami.hibernate.log4jdbc.sql.rdbmsspecifics.RdbmsSpecifics;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * RDBMS specifics for the IBM DB2.
 */
public class Db2RdbmsSpecifics extends RdbmsSpecifics {

	private static final String DATE_FORMAT = "'TIMESTAMP('''yyyy-MM-dd HH:mm:ss.SSS''')'";

	public Db2RdbmsSpecifics() {
		super();
	}

	@Override
	public String formatParameterObject(Object object) {
		if (object instanceof Date) {
			return new SimpleDateFormat(DATE_FORMAT).format((Date) object);
		} 
		return super.formatParameterObject(object);
	}
}
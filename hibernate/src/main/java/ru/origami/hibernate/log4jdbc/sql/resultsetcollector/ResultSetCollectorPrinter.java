package ru.origami.hibernate.log4jdbc.sql.resultsetcollector;

import java.util.List;

public class ResultSetCollectorPrinter {

    /**
     * A StringBuffer which is used to build the formatted table to print
     */
    private StringBuffer table = new StringBuffer(); ;

    /**
     * Default constructor
     */
    public ResultSetCollectorPrinter() {

    }

    /***
     * Return a table which represents a <code>ResultSet</code>,
     * to be printed by a logger,
     * based on the content of the provided <code>resultSetCollector</code>.
     *
     * This method will be actually called by a <code>SpyLogDelegator</code>
     * when the <code>next()</code> method of the spied <code>ResultSet</code>
     * return <code>false</code> meaning that its end is reached.
     * It will be also called if the <code>ResultSet</code> is closed.
     *
     *
     * @param resultSetCollector the ResultSetCollector which has collected the data we want to print
     * @return A <code>String</code> which contains the formatted table to print
     *
     * @see ru.origami.hibernate.log4jdbc.ResultSetSpy
     * @see ru.origami.hibernate.log4jdbc.sql.resultsetcollector.DefaultResultSetCollector
     * @see ru.origami.hibernate.log4jdbc.log.SpyLogDelegator
     *
     */
    public String getResultSetToPrint(ResultSetCollector resultSetCollector) {

        this.table.append(System.getProperty("line.separator"));

        int columnCount = resultSetCollector.getColumnCount();
        int maxLength[] = new int[columnCount];

        for (int column = 1; column <= columnCount; column++) {
            String columnName = resultSetCollector.getColumnName(column);
            maxLength[column - 1] = columnName != null ? columnName.length() : 0;
        }
        if (resultSetCollector.getRows() != null) {
            for (List<Object> printRow : resultSetCollector.getRows()) {
                int colIndex = 0;
                for (Object v : printRow) {
                    if (v != null) {
                        int length = v.toString().length();
                        if (length > maxLength[colIndex]) {
                            maxLength[colIndex] = length;
                        }
                    }
                    colIndex++;
                }
            }
        }
        for (int column = 1; column <= columnCount; column++) {
            maxLength[column - 1] = maxLength[column - 1] + 1;
        }

        this.table.append("|");

        for (int column = 1; column <= columnCount; column++) {
            this.table.append(padRight("-", maxLength[column - 1]).replaceAll(" ", "-")
                    + "|");
        }
        this.table.append(System.getProperty("line.separator"));
        this.table.append("|");
        for (int column = 1; column <= columnCount; column++) {
            String columnName = resultSetCollector.getColumnName(column);
            this.table.append(padRight(columnName != null ? columnName : "",
                maxLength[column - 1]) + "|");
        }
        this.table.append(System.getProperty("line.separator"));
        this.table.append("|");
        for (int column = 1; column <= columnCount; column++) {
            this.table.append(padRight("-", maxLength[column - 1]).replaceAll(" ", "-")
                    + "|");
        }
        this.table.append(System.getProperty("line.separator"));
        if (resultSetCollector.getRows() != null) {
            for (List<Object> printRow : resultSetCollector.getRows()) {
                int colIndex = 0;
                this.table.append("|");
                for (Object v : printRow) {
                    this.table.append(padRight(v == null ? "null" : v.toString(),
                            maxLength[colIndex])
                            + "|");
                    colIndex++;
                }
                this.table.append(System.getProperty("line.separator"));
            }
        }
        this.table.append("|");
        for (int column = 1; column <= columnCount; column++) {
            this.table.append(padRight("-", maxLength[column - 1]).replaceAll(" ", "-")
                    + "|");
        }

        this.table.append(System.getProperty("line.separator"));

        resultSetCollector.reset();

        return this.table.toString();

    }

    /***
     * Add space to the provided <code>String</code> to match the provided width
     * @param s the <code>String</code> we want to adjust
     * @param n the width of the returned <code>String</code>
     * @return a <code>String</code> matching the provided width
     */
    private static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }
}

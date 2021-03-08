package ionshield.expertsystem.core;

public abstract class Config {
    public static abstract class InputTable {
        public static String[] COLUMNS = new String[]{"Name", "Type", "Value"};
        public static int getIndex(String header) {
            if (header != null) {
                for (int i = 0; i < COLUMNS.length; i++) {
                    String s = COLUMNS[i];
                    if (s.equalsIgnoreCase(header)) return i;
                }
            }
            throw new IllegalArgumentException("No such table header: " + header);
        }
    }

    public static abstract class OutputTable {
        public static String[] COLUMNS = new String[]{"Name", "Type", "Value"};
        public static int getIndex(String header) {
            if (header != null) {
                for (int i = 0; i < COLUMNS.length; i++) {
                    String s = COLUMNS[i];
                    if (s.equalsIgnoreCase(header)) return i;
                }
            }
            throw new IllegalArgumentException("No such table header: " + header);
        }
    }

    public static abstract class InputSettingsTable {
        public static String[] COLUMNS = new String[]{"Name", "Type", "Enum values"};
        public static int getIndex(String header) {
            if (header != null) {
                for (int i = 0; i < COLUMNS.length; i++) {
                    String s = COLUMNS[i];
                    if (s.equalsIgnoreCase(header)) return i;
                }
            }
            throw new IllegalArgumentException("No such table header: " + header);
        }
    }

    public static abstract class OutputSettingsTable {
        public static String[] COLUMNS = (new String[]{"Name", "Type", "Enum values"});
        public static int getIndex(String header) {
            if (header != null) {
                for (int i = 0; i < COLUMNS.length; i++) {
                    String s = COLUMNS[i];
                    if (s.equalsIgnoreCase(header)) return i;
                }
            }
            throw new IllegalArgumentException("No such table header: " + header);
        }
    }

    public static abstract class RulesSettingsTable {
        public static String[] COLUMNS = new String[]{"If", "Then", "Else"};
        public static int getIndex(String header) {
            if (header != null) {
                for (int i = 0; i < COLUMNS.length; i++) {
                    String s = COLUMNS[i];
                    if (s.equalsIgnoreCase(header)) return i;
                }
            }
            throw new IllegalArgumentException("No such table header: " + header);
        }
    }
}

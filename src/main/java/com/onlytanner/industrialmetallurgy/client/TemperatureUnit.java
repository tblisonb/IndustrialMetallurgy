package com.onlytanner.industrialmetallurgy.client;

// Every machine stores its temperature internally as a plain Fahrenheit-scale int (see
// BasicForgeBlockEntity/AdvancedForgeBlockEntity's maxTemperature values, e.g. 2000-4200) -- this
// only controls how that value is displayed. The current unit is a single client-side preference
// shared by every forge screen (a button toggles it, see BasicForgeScreen/AdvancedForgeScreen),
// not persisted to disk -- it resets to Fahrenheit each session.
public enum TemperatureUnit {

    FAHRENHEIT("°F") {
        @Override
        public int convert(int fahrenheit) {
            return fahrenheit;
        }
    },
    CELSIUS("°C") {
        @Override
        public int convert(int fahrenheit) {
            return Math.round((fahrenheit - 32) * 5f / 9f);
        }
    };

    private static TemperatureUnit current = FAHRENHEIT;

    private final String suffix;

    TemperatureUnit(String suffix) {
        this.suffix = suffix;
    }

    public abstract int convert(int fahrenheit);

    public String format(int fahrenheit) {
        return this.convert(fahrenheit) + this.suffix;
    }

    public String symbol() {
        return this.suffix;
    }

    public static TemperatureUnit current() {
        return current;
    }

    public static void toggle() {
        current = current == FAHRENHEIT ? CELSIUS : FAHRENHEIT;
    }

}

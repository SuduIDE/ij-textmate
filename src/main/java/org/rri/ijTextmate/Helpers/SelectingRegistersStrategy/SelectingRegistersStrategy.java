package org.rri.ijTextmate.Helpers.SelectingRegistersStrategy;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface SelectingRegistersStrategy {
    String apply(String word);

    SelectingRegistersStrategy DEFAULT = new DefaultStrategySelectingRegisters();
    SelectingRegistersStrategy UPPER = new UpperStrategySelectingRegisters();
    @SuppressWarnings("unused")
    SelectingRegistersStrategy LOWER = new LowerStrategySelectingRegisters();

    class DefaultStrategySelectingRegisters implements SelectingRegistersStrategy {
        @Override
        public String apply(@NotNull String word) {
            return word;
        }
    }


    class UpperStrategySelectingRegisters implements SelectingRegistersStrategy {
        @Override
        public String apply(@NotNull String word) {
            return word.toUpperCase();
        }
    }

    class LowerStrategySelectingRegisters implements SelectingRegistersStrategy {
        @Override
        public String apply(@NotNull String word) {
            return word.toLowerCase();
        }
    }
}
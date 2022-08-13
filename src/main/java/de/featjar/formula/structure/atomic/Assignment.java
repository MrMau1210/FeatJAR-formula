/*
 * Copyright (C) 2022 Sebastian Krieter, Elias Kuiter
 *
 * This file is part of formula.
 *
 * formula is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 *
 * formula is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with formula. If not, see <https://www.gnu.org/licenses/>.
 *
 * See <https://github.com/FeatJAR/formula> for further information.
 */
package de.featjar.formula.structure.atomic;

import de.featjar.util.data.Pair;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface Assignment {

    default void setAll(Collection<Pair<Integer, Object>> assignments) {
        for (final Pair<Integer, Object> pair : assignments) {
            set(pair.getKey(), pair.getValue());
        }
    }

    default void unsetAll(Collection<Pair<Integer, Object>> assignments) {
        for (final Pair<Integer, Object> pair : assignments) {
            set(pair.getKey(), null);
        }
    }

    void unsetAll();

    default void unset(int index) {
        set(index, null);
    }

    void set(int index, Object assignment);

    Optional<Object> get(int index);

    List<Pair<Integer, Object>> getAll();
}

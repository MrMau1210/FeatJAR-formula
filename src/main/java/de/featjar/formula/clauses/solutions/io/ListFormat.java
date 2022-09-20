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
 * See <https://github.com/FeatureIDE/FeatJAR-formula> for further information.
 */
package de.featjar.formula.clauses.solutions.io;

import de.featjar.formula.clauses.LiteralList;
import de.featjar.formula.clauses.VariableMap;
import de.featjar.formula.clauses.solutions.SolutionList;
import de.featjar.base.data.Problem.Severity;
import de.featjar.base.data.Result;
import de.featjar.base.io.InputMapper;
import de.featjar.base.io.format.Format;
import de.featjar.base.io.format.ParseProblem;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Reads / Writes a list of configuration.
 *
 * @author Sebastian Krieter
 */
public class ListFormat implements Format<SolutionList> {

    @Override
    public String serialize(SolutionList configurationList) {
        final StringBuilder csv = new StringBuilder();
        csv.append("Configuration");
        final List<String> names = configurationList.getVariableMap().getVariableNames();
        for (final String name : names) {
            csv.append(';');
            csv.append(name);
        }
        csv.append('\n');
        int configurationIndex = 0;
        for (final LiteralList configuration : configurationList.getSolutions()) {
            csv.append(configurationIndex++);
            final int[] literals = configuration.getLiterals();
            for (int i = 0; i < literals.length; i++) {
                csv.append(';');
                csv.append(literals[i] < 0 ? 0 : 1);
            }
            csv.append('\n');
        }
        return csv.toString();
    }

    @Override
    public Result<SolutionList> parse(InputMapper inputMapper) {
        int lineNumber = 0;
        final SolutionList configurationList = new SolutionList();
        final Iterator<String> iterator = inputMapper.get().getLineStream().iterator();
        try {
            {
                if (!iterator.hasNext()) {
                    return Result.empty(new ParseProblem("Empty file!", lineNumber, Severity.ERROR));
                }
                final String line = iterator.next();
                if (line.trim().isEmpty()) {
                    return Result.empty(new ParseProblem("Empty file!", lineNumber, Severity.ERROR));
                }
                final String[] names = line.split(";");
                final VariableMap map = VariableMap.empty();
                Arrays.asList(names).subList(1, names.length).forEach(map::add);
                configurationList.setVariables(map);
            }

            while (iterator.hasNext()) {
                final String line = iterator.next();
                lineNumber++;
                final String[] split = line.split(";");
                if ((split.length - 1)
                        != configurationList
                                .getVariableMap()
                                .getVariableNames()
                                .size()) {
                    return Result.empty(new ParseProblem(
                            "Number of selections does not match number of features!", lineNumber, Severity.ERROR));
                }
                final int[] literals = new int
                        [configurationList
                                .getVariableMap()
                                .getVariableNames()
                                .size()];
                for (int i = 1; i < split.length; i++) {
                    literals[i - 1] = split[i].equals("0") ? -i : i;
                }
                configurationList.addSolution(new LiteralList(literals, LiteralList.Order.INDEX, false));
            }
        } catch (final Exception e) {
            return Result.empty(new ParseProblem(e.getMessage(), lineNumber, Severity.ERROR));
        }
        return Result.of(configurationList);
    }

    @Override
    public Optional<String> getFileExtension() {
        return Optional.of("csv");
    }

    @Override
    public ListFormat getInstance() {
        return this;
    }

    @Override
    public String getIdentifier() {
        return ID;
    }

    @Override
    public boolean supportsSerialize() {
        return true;
    }

    @Override
    public boolean supportsParse() {
        return true;
    }

    @Override
    public String getName() {
        return "ConfigurationList";
    }
}

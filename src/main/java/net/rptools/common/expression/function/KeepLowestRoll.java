/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.rptools.common.expression.function;

import java.math.BigDecimal;
import java.util.List;
import net.rptools.parser.Parser;
import net.rptools.parser.function.AbstractNumberFunction;
import net.rptools.parser.function.EvaluationException;

public class KeepLowestRoll extends AbstractNumberFunction {

	public KeepLowestRoll() {
		super(3, 3, false, "keepLowest");
	}

	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> parameters) throws EvaluationException {
		int n = 0;
		int times = ((BigDecimal) parameters.get(n++)).intValue();
		int sides = ((BigDecimal) parameters.get(n++)).intValue();
		int keep = ((BigDecimal) parameters.get(n++)).intValue();

		return new BigDecimal(DiceHelper.keepLowestDice(times, sides, keep));
	}

}

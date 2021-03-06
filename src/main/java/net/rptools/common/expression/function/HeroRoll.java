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

import net.rptools.common.expression.RunData;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.VariableResolver;
import net.rptools.parser.function.AbstractNumberFunction;

/*
 * Hero System Dice
 * 
 * Used to get both the stun & body of an attack roll.
 * 
 */
public class HeroRoll extends AbstractNumberFunction {
	public HeroRoll() {
		super(2, 2, false, "hero", "herostun", "herobody");
	}

	// Use variable names with illegal character to minimize chances of variable overlap
	private static String lastTimesVar = "#Hero-LastTimesVar";
	private static String lastSidesVar = "#Hero-LastSidesVar";
	private static String lastBodyVar = "#Hero-LastBodyVar";

	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> parameters) throws ParserException {
		int n = 0;

		double times = ((BigDecimal) parameters.get(n++)).doubleValue();
		int sides = ((BigDecimal) parameters.get(n++)).intValue();

		VariableResolver vr = parser.getVariableResolver();

		if (functionName.equalsIgnoreCase("herobody")) {
			double lastTimes = 0;
			if (vr.containsVariable(lastTimesVar))
				lastTimes = ((BigDecimal) vr.getVariable(lastTimesVar)).doubleValue();

			int lastSides = 0;
			if (vr.containsVariable(lastSidesVar))
				lastSides = ((BigDecimal) vr.getVariable(lastSidesVar)).intValue();

			int lastBody = 0;
			if (vr.containsVariable(lastBodyVar))
				lastBody = ((BigDecimal) vr.getVariable(lastBodyVar)).intValue();

			if (times == lastTimes && sides == lastSides)
				return new BigDecimal(lastBody);

			return new BigDecimal(-1); // Should this be -1?  Perhaps it should return null.
		} else {
			// assume stun

			double lastTimes = times;
			int lastSides = sides;
			int lastBody = 0;

			RunData runData = RunData.getCurrent();

			int stun = 0;
			double half = times - Math.floor(times);
			for (int i = 0; i < Math.floor(times); i++) {
				int die = runData.randomInt(sides);
				/*
				 * Keep track of the body generated.  In theory
				 * Hero System only uses 6-sided where a 1 is 
				 * 0 body, 2-5 is 1 body and 6 is 2 body but I 
				 * left the sides unbounded just in case.
				 */
				if (die > 1)
					lastBody++;
				if (die == sides)
					lastBody++;

				stun += die;
			}

			if (half >= 0.5) {
				/*
				 * Roll a half dice.  In theory Hero System 
				 * only uses 6-sided and for half dice
				 * 1 & 2 = 1 Stun 0 body
				 * 3     = 2 stun 0 body
				 * 4     = 2 stun 1 body
				 * 5 & 6 = 3 stun 1 body
				 */
				int die = runData.randomInt(sides);
				if (die * 2 > sides)
					lastBody++;

				stun += (die + 1) / 2;
			}

			parser.setVariable(lastTimesVar, new BigDecimal(lastTimes));
			parser.setVariable(lastSidesVar, new BigDecimal(lastSides));
			parser.setVariable(lastBodyVar, new BigDecimal(lastBody));

			return new BigDecimal(stun);
		}
	}
}

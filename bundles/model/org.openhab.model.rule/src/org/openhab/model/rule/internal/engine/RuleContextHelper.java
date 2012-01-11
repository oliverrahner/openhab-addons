/**
 * openHAB, the open Home Automation Bus.
 * Copyright (C) 2010-2012, openHAB.org <admin@openhab.org>
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or
 * combining it with Eclipse (or a modified version of that library),
 * containing parts covered by the terms of the Eclipse Public License
 * (EPL), the licensors of this Program grant you additional permission
 * to convey the resulting work.
 */
package org.openhab.model.rule.internal.engine;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.xbase.interpreter.IEvaluationContext;
import org.openhab.model.rule.RulesStandaloneSetup;
import org.openhab.model.rule.rules.Rule;
import org.openhab.model.rule.rules.RuleModel;
import org.openhab.model.rule.rules.Variable;

import com.google.inject.Provider;

/**
 * Helper class to deal with rule evaluation contexts.
 * 
 * @author Kai Kreuzer
 * @since 0.9.0
 *
 */
@SuppressWarnings("restriction")
public class RuleContextHelper {

	private static Provider<IEvaluationContext> contextProvider = RulesStandaloneSetup.getInjector().getProvider(IEvaluationContext.class);

	public static IEvaluationContext getContext(Rule rule) {
	    RuleModel ruleModel = (RuleModel) rule.eContainer();

	    // check if a context already exists on the resource
	    for(Adapter adapter : ruleModel.eAdapters()) {
			if(adapter instanceof RuleContextAdapter) {
				return ((RuleContextAdapter) adapter).getContext();
			}
		}
		
		// no evaluation context found, so create a new one
	    IEvaluationContext evaluationContext = contextProvider.get();
	    for(Variable var : ruleModel.getVariables()) {
	    	evaluationContext.newValue(QualifiedName.create(var.getName()), null);
	    }
	    ruleModel.eAdapters().add(new RuleContextAdapter(evaluationContext));
		return evaluationContext;
	}

	/**
	 * Inner class that wraps an evaluation context into an EMF adapters
	 */
	private static class RuleContextAdapter extends EContentAdapter {
		
		private IEvaluationContext context;

		public RuleContextAdapter(IEvaluationContext context) {
			this.context = context;
		}

		public IEvaluationContext getContext() {
			return context;
		}

	}

}

package com.eric.codejam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;


public class AddTerms extends AbstractTerm {
    
    List<Term> terms;
    
    AddTerms() {
        terms = new ArrayList<>();
    }
    AddTerms(List<Term> args) {
        terms = new ArrayList<>();
        terms.addAll(args);
        Collections.sort(terms, new Polynomial.CompareTerm());
        
        terms = ImmutableList.copyOf(terms);
        
    }
    
    @Override
    public void substitute(VariableTerm old, Term newTerm) {
        for(ListIterator<Term> li = terms.listIterator(); li.hasNext();) {
            Term t = li.next();
            if (t.equals(old)) {
                li.set(newTerm);
            } else {
                t.substitute(old, newTerm);
            }
        }
    }

   
    
    @Override
    public int evaluate(Map<String, Integer> values) {
        int r = 0;
        
        for(Term term : terms) {
            r += term.evaluate(values);
        }
        
        return r;
    }

   

    @Override
    public Term simplify() {
                
        List<Term> simTerms = new ArrayList<>();
        simTerms.addAll(getTerms());
        boolean hasSimp = false;
        
        // Simplify any sub elements
        for (ListIterator<Term> li = simTerms.listIterator(); li.hasNext();) {
            Term t = li.next();

            Term r = t.simplify();
            if (r != null) {
            
                li.set(r);
                hasSimp = true;
            }
        }
        
        boolean found = true;
        while (found) {
            found = false;

            for (int i = 0; i < simTerms.size(); ++i) {
                for (int j = i + 1; j < simTerms.size(); ++j) {

                    Term lhs = simTerms.get(i);
                    Term rhs = simTerms.get(j);
                    Term replacement = null;
                    
                    if (!lhs.canAdd(rhs)) {
                        continue;
                    }
                    
                    replacement = lhs.add(rhs);
                    
                    Preconditions.checkState(replacement != null);
                    simTerms.remove(j);
                    simTerms.remove(i);

                    simTerms.add(replacement);
                    found = true;
                    hasSimp = true;
                    break;
                
                }

                if (found) {
                    break;
                }
            }

        }
       

       if (hasSimp) {
           return new AddTerms(simTerms);
       }

        return null;
        
        
    }
    
    @Override
    public boolean canAdd(Term rhs) {
        return rhs.canAddAsRhs(this);
    }
    @Override
    public Term add(Term rhs) {
        return rhs.addAsRhs(this);
    }
    
    @Override
    public Term multiply(Term rhs) {
        return rhs.multiplyAsRhs(this);
    }
    public Term multiplyAsRhsImpl(Term lhs) {
        List<Term> terms = new ArrayList<>();
        for(Term term : getTerms()) {
            List<Term> mTerms = new ArrayList<>();
            mTerms.add(lhs);
            mTerms.add(term);
            MultTerms m = new MultTerms(mTerms);
            terms.add(m);
        }

        return new AddTerms(terms);
    }
    @Override
    public Term multiplyAsRhs(MultTerms lhs) {
        return multiplyAsRhsImpl(lhs);
    }
   
    @Override
    public boolean canMultiplyAsRhs(MultTerms lhs) {
        return true;
    }
    @Override
    public Term multiplyAsRhs(CoefficientTerm lhs) {
        return multiplyAsRhsImpl(lhs);
    }
    @Override
    public Term multiplyAsRhs(VariableTerm lhs) {
        return multiplyAsRhsImpl(lhs);
    }
    @Override
    public boolean canMultiplyAsRhs(PowerTerm lhs) {
        return true;
    }
    @Override
    public Term multiplyAsRhs(PowerTerm lhs) {
        return multiplyAsRhsImpl(lhs);
    }
    @Override
    public boolean canMultiplyAsRhs(VariableTerm lhs) {
        return true;
    }
    @Override
    public boolean canMultiplyAsRhs(CoefficientTerm lhs) {
        return true;
    }
    @Override
    public boolean canMultiply(Term rhs) {
        return rhs.canMultiply(this);
    }
    @Override
    public boolean canAddAsRhs(AddTerms lhs) {
        return true;
    }
    @Override
    public Term addAsRhs(AddTerms lhs) {
        List<Term> terms = new ArrayList<>();
        terms.addAll(lhs.getTerms());
        terms.addAll(this.getTerms());
        return new AddTerms(terms);
    }
    public List<Term> getTerms() {
        return terms;
    }
    @Override
    public String toString() {
        return StringUtils.join(terms, " + ");        
    }
}

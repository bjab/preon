/**
 * Copyright (c) 2009-2016 Wilfred Springer
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package org.codehaus.preon.el.ast;

import org.codehaus.preon.el.BindingException;
import org.codehaus.preon.el.Reference;
import org.codehaus.preon.el.ReferenceContext;

import java.util.Set;

/**
 * A {@link Node} representing a combinatorial boolean operator.
 * 
 * @author Wilfred Springer
 * 
 * @param <E>
 *            The type of context on which this node would be applied.
 */
public class BooleanOperatorNode<E> extends AbstractNode<Boolean, E> {

    /**
     * The left-hand side of the operation.
     */
    private Node<Boolean, E> lhs;

    /**
     * The right-hand side of the operation.
     */
    private Node<Boolean, E> rhs;

    /**
     * The operator to apply.
     */
    private BooleanOperator operator;

    public enum BooleanOperator {

        AND {
            <E> boolean holds(E context, Node<Boolean, E> lhs, Node<Boolean, E> rhs) {
                return lhs.eval(context) && rhs.eval(context);
            }
        },

        OR {
            <E> boolean holds(E context, Node<Boolean, E> lhs, Node<Boolean, E> rhs) {
                return lhs.eval(context) || rhs.eval(context);
            }
        };

        abstract <E> boolean holds(E context, Node<Boolean, E> lhs, Node<Boolean, E> rhs);
    }

    /**
     * Constructs a new instance, accepting the operator, and the left-hand and
     * right-hand side.
     * 
     * @param operator
     *            The operator to apply.
     * @param lhs
     *            The left-hand side of the operation.
     * @param rhs
     *            The right-hand side of the operation.
     */
    public BooleanOperatorNode(BooleanOperator operator, Node<Boolean, E> lhs, Node<Boolean, E> rhs) {
        this.operator = operator;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    /**
     * @see org.codehaus.preon.el.ast.Node#eval(java.lang.Object)
     */
    public Boolean eval(E context) {
        return operator.holds(context, lhs, rhs);
    }

    /**
     * @see org.codehaus.preon.el.ast.Node#getType()
     */
    public Class<Boolean> getType() {
        return Boolean.class;
    }

    /**
     * @see org.codehaus.preon.el.ast.Node#simplify()
     */
    public Node<Boolean, E> simplify() {
        return this;
    }

    public Node<Boolean, E> rescope(ReferenceContext<E> context) {
        return this;
    }

    /**
     * @see org.codehaus.preon.el.ast.Node#gather(java.util.Set)
     */
    public void gather(Set<Reference<E>> references) {
        lhs.gather(references);
        rhs.gather(references);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.Expression#isParameterized()
     */
    public boolean isParameterized() {
        return lhs.isParameterized() || rhs.isParameterized();
    }

    /**
     * Constructs a new {@link BooleanOperatorNode}.
     * 
     * @param <E>
     * @param operator
     * @param lhs
     * @param rhs
     * @return
     */
    public static <E> BooleanOperatorNode<E> create(BooleanOperator operator, Node<?, E> lhs,
            Node<?, E> rhs) {
        Node<Boolean, E> booleanLhs = createBooleanNode(lhs);
        Node<Boolean, E> booleanRhs = createBooleanNode(rhs);
        return new BooleanOperatorNode<E>(operator, booleanLhs, booleanRhs);
    }

    /**
     * Creates a boolean {@link Node} from the untyped {@link Node} passed in.
     * 
     * @param <E> The type of context on which the node will be applied.
     * @param node The untyped node.
     * @return A boolean node.
     */
    public static <E> Node<Boolean, E> createBooleanNode(Node<?, E> node) {
        if (!boolean.class.isAssignableFrom(node.getType())
                && !Boolean.class.isAssignableFrom(node.getType())) {
            throw new BindingException("Reference ADD_LABEL_HERE  does not resolve to boolean.");
        } else {
            return (Node<Boolean, E>) node;
        }
    }

    @Override
    public boolean isConstantFor(ReferenceContext<E> eReferenceContext) {
        return lhs.isConstantFor(eReferenceContext) && rhs.isConstantFor(eReferenceContext);

    }
}

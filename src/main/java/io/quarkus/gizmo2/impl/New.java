package io.quarkus.gizmo2.impl;

import java.lang.constant.ClassDesc;

import io.github.dmlloyd.classfile.CodeBuilder;

final class New extends Item {
    private final ClassDesc type;
    private Item invokeItem;

    New(final ClassDesc type) {
        this.type = type;
    }

    void setInvokeItem(Item invokeItem) {
        this.invokeItem = invokeItem;
    }

    public ClassDesc type() {
        return type;
    }

    @Override
    public Node pop(final Node node) {
        // inserting `Pop` after the `New` node and keeping everything after it (`Dup`, constructor args, `Invoke`)
        // intact is wrong -- we need to insert `Pop` after the `Invoke`
        Node current = node;
        while (current != null && current.item() != invokeItem) {
            current = current.next();
        }
        assert current != null;

        Pop pop = new Pop(this);
        pop.insert(current.next());
        // `New` has no dependencies, return right away
        return node.prev();
    }

    public void writeCode(final CodeBuilder cb, final BlockCreatorImpl block) {
        cb.new_(type);
    }
}

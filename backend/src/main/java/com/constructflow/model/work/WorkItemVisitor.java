package com.constructflow.model.work;

public interface WorkItemVisitor {
    void visitLeaf(LeafTask task);
    void visitComposite(CompositeTask composite);
}

class LockingTree {
    private Node root;
    private Map<String, Node> labelToNode;
    private List<String> outputLog;
    private Set<Node> lockedNodes; // Track locked nodes

    LockingTree(Node treeRoot) {
        root = treeRoot;
        labelToNode = new HashMap<>();
        outputLog = new ArrayList<>();
        lockedNodes = new HashSet<>();
    }

    // Other methods remain the same

    boolean lockNode(String label, int id) {
        Node targetNode = labelToNode.get(label);

        if (targetNode.isLocked || targetNode.descendant != 0 || !canLock(targetNode)) {
            return false;
        }

        targetNode.isLocked = true;
        targetNode.userID = id;
        lockedNodes.add(targetNode); // Add to locked nodes

        // Update ancestor lock counts
        Node currentNode = targetNode.parent;
        while (currentNode != null) {
            currentNode.descendantLocked++;
            currentNode = currentNode.parent;
        }

        return true;
    }

    boolean unlockNode(String label, int id) {
        Node targetNode = labelToNode.get(label);

        if (!targetNode.isLocked || targetNode.userID != id) {
            return false;
        }

        targetNode.isLocked = false;
        lockedNodes.remove(targetNode); // Remove from locked nodes

        // Update ancestor lock counts
        Node currentNode = targetNode.parent;
        while (currentNode != null) {
            currentNode.descendantLocked--;
            currentNode = currentNode.parent;
        }

        return true;
    }

    // boolean upgradeNode(String label, int id) {
    //     Node targetNode = labelToNode.get(label);

    //     if (targetNode.isLocked || !canUpgrade(targetNode)) {
    //         return false;
    //     }

    //     List<Node> lockedDescendants = new ArrayList<>();
    //     collectLockedDescendants(targetNode, lockedDescendants, id);

    //     // Unlock all locked descendants
    //     for (Node lockedDescendant : lockedDescendants) {
    //         unlockNode(lockedDescendant.label, id);
    //     }

    //     return lockNode(label, id);
    // }
    boolean upgradeNode(String label, int id) {
    Node targetNode = labelToNode.get(label);

    if (targetNode.isLocked || !canUpgrade(targetNode)) {
        return false;
    }

    List<Node> lockedDescendants = new ArrayList<>();
    collectLockedDescendants(targetNode, lockedDescendants, id);

    if (lockedDescendants.isEmpty()) {
        return false;
    }

    for (Node lockedDescendant : lockedDescendants) {
        unlockNode(lockedDescendant.label, lockedDescendant.userID);
    }

    return lockNode(label, id);
}

    private boolean canLock(Node node) {
        // Check if any ancestors are locked
        Node currentNode = node.parent;
        while (currentNode != null) {
            if (lockedNodes.contains(currentNode)) {
                return false;
            }
            currentNode = currentNode.parent;
        }
        return true;
    }

    private boolean canUpgrade(Node node) {
        // Check if any ancestors are locked and if any descendants are locked
        Node currentNode = node.parent;
        while (currentNode != null) {
            if (lockedNodes.contains(currentNode)) {
                return false;
            }
            currentNode = currentNode.parent;
        }
        return node.descendantLocked > 0;
    }

    private void collectLockedDescendants(Node currentNode, List<Node> lockedDescendants, int id) {
        if (currentNode.isLocked && currentNode.userID != id) {
            lockedDescendants.add(currentNode);
        }
        for (Node child : currentNode.children) {
            collectLockedDescendants(child, lockedDescendants, id);
        }
    }
}

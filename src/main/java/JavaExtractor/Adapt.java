package JavaExtractor;

import JavaExtractor.FeaturesEntities.Property;
import com.github.javaparser.ast.DataKey;
import com.github.javaparser.ast.Node;

import java.util.Optional;

public class Adapt {
    public static Property getData(Optional<Node> node, DataKey<Property> propertyKey) {
        if (node.isPresent())
            return getData(node.get(), propertyKey);
        return null;
    }

    public static Property getData(Node node, DataKey<Property> propertyKey) {
        if (node.containsData(propertyKey))
            return node.getData(propertyKey);
        return null;
    }

    public static Node getParentNode(Node current) {
        if (current.getParentNode().isPresent())
            return current.getParentNode().get();
        return null;
    }
}

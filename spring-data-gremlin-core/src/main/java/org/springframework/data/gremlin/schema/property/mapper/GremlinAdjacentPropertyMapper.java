package org.springframework.data.gremlin.schema.property.mapper;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.schema.property.GremlinAdjacentProperty;
import org.springframework.data.gremlin.schema.property.GremlinLinkProperty;

import java.util.Map;
import java.util.function.Consumer;

/**
 * A {@link GremlinPropertyMapper} for mapping {@link GremlinAdjacentProperty}s.
 *
 * @author Gman
 */
public class GremlinAdjacentPropertyMapper implements GremlinPropertyMapper<GremlinAdjacentProperty, Edge> {

    @Override
    public void copyToVertex(final GremlinAdjacentProperty property, final GremlinGraphAdapter graphAdapter, final Edge edge, final Object val, final  Map<Object, Object> cascadingSchemas) {

        Vertex linkedVertex = edge.vertices(property.getDirection()).next();

        if (linkedVertex == null) {
            linkedVertex = (Vertex) cascadingSchemas.get(val);
        }

        if (linkedVertex != null && (Boolean.getBoolean(CASCADE_ALL_KEY) || property.getDirection() == Direction.OUT)) {
            //             Updates or saves the val into the linkedVertex
            property.getRelatedSchema().cascadeCopyToGraph(graphAdapter, linkedVertex, val, cascadingSchemas);
        }
    }

    @Override
    public <K> Object loadFromVertex(final GremlinAdjacentProperty property, final Edge edge, final Map<Object, Object> cascadingSchemas) {
            Object val = null;
            Vertex linkedVertex = edge.vertices(property.getDirection()).next();
            if (linkedVertex != null) {
                val = property.getRelatedSchema().cascadeLoadFromGraph(linkedVertex, cascadingSchemas);
            }
            return val;
    }
}

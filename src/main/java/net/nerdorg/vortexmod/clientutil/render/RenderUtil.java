package net.nerdorg.vortexmod.clientutil.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class RenderUtil {
    public static void drawLine(MultiBufferSource pBuffer, PoseStack pPoseStack, Vector3f startPoint, Vector3f endPoint, int width, int alpha, int r, int g, int b) {
        VertexConsumer vertexBuilder = pBuffer.getBuffer(ModRenderType.getLineOfWidth(width));
        Matrix4f positionMatrix = pPoseStack.last().pose();

        vertexBuilder.vertex(positionMatrix, startPoint.x(), startPoint.y(), startPoint.z())
                .color(r, g, b, alpha)
                .normal(1, 0, 0) // Adjusted normal for clarity
                .endVertex();

        vertexBuilder.vertex(positionMatrix, endPoint.x(), endPoint.y(), endPoint.z())
                .color(r, g, b, alpha)
                .normal(1, 0, 0) // Adjusted normal for clarity
                .endVertex();
    }

    public static void drawSphere(MultiBufferSource pBuffer, PoseStack pPoseStack, Vector3f center, float radius, int segments, int alpha, int r, int g, int b) {
        VertexConsumer vertexBuilder = pBuffer.getBuffer(RenderType.debugQuads());
        Matrix4f positionMatrix = pPoseStack.last().pose();

        for (int i = 0; i < segments; i++) {
            float theta1 = (float) (Math.PI * i / segments);
            float theta2 = (float) (Math.PI * (i + 1) / segments);

            for (int j = 0; j < 2 * segments; j++) {
                float phi1 = (float) (Math.PI * 2 * j / (2 * segments));
                float phi2 = (float) (Math.PI * 2 * (j + 1) / (2 * segments));

                Vector3f point1 = new Vector3f(
                        (float) (center.x() + radius * Math.sin(theta1) * Math.cos(phi1)),
                        (float) (center.y() + radius * Math.cos(theta1)),
                        (float) (center.z() + radius * Math.sin(theta1) * Math.sin(phi1))
                );
                Vector3f point2 = new Vector3f(
                        (float) (center.x() + radius * Math.sin(theta1) * Math.cos(phi2)),
                        (float) (center.y() + radius * Math.cos(theta1)),
                        (float) (center.z() + radius * Math.sin(theta1) * Math.sin(phi2))
                );
                Vector3f point3 = new Vector3f(
                        (float) (center.x() + radius * Math.sin(theta2) * Math.cos(phi1)),
                        (float) (center.y() + radius * Math.cos(theta2)),
                        (float) (center.z() + radius * Math.sin(theta2) * Math.sin(phi1))
                );
                Vector3f point4 = new Vector3f(
                        (float) (center.x() + radius * Math.sin(theta2) * Math.cos(phi2)),
                        (float) (center.y() + radius * Math.cos(theta2)),
                        (float) (center.z() + radius * Math.sin(theta2) * Math.sin(phi2))
                );

                // Draw the quad strip for the sphere
                vertexBuilder.vertex(positionMatrix, point1.x(), point1.y(), point1.z())
                        .color(r, g, b, alpha)
                        .normal(1, 0, 0)
                        .endVertex();
                vertexBuilder.vertex(positionMatrix, point2.x(), point2.y(), point2.z())
                        .color(r, g, b, alpha)
                        .normal(1, 0, 0)
                        .endVertex();
                vertexBuilder.vertex(positionMatrix, point4.x(), point4.y(), point4.z())
                        .color(r, g, b, alpha)
                        .normal(1, 0, 0)
                        .endVertex();
                vertexBuilder.vertex(positionMatrix, point3.x(), point3.y(), point3.z())
                        .color(r, g, b, alpha)
                        .normal(1, 0, 0)
                        .endVertex();
            }
        }
    }

    public static void drawDonut(MultiBufferSource pBuffer, PoseStack pPoseStack, Vector3f center, float innerRadius, float outerRadius, int segments, float xRotation, float yRotation, int alpha, int r, int g, int b) {
        VertexConsumer vertexBuilder = pBuffer.getBuffer(RenderType.debugQuads());
        Matrix4f positionMatrix = pPoseStack.last().pose();

        // Precompute rotation matrices for the X and Y rotations
        Matrix3f xRotationMatrix = new Matrix3f().rotateX((float) Math.toRadians(xRotation));
        Matrix3f yRotationMatrix = new Matrix3f().rotateY((float) Math.toRadians(yRotation));

        for (int i = 0; i < segments; i++) {
            float theta1 = (float) (Math.PI * 2 * i / segments);
            float theta2 = (float) (Math.PI * 2 * (i + 1) / segments);

            for (int j = 0; j < segments; j++) {
                float phi1 = (float) (Math.PI * 2 * j / segments);
                float phi2 = (float) (Math.PI * 2 * (j + 1) / segments);

                // Calculate the points on the torus
                Vector3f point1 = calculateTorusPoint(center, innerRadius, outerRadius, theta1, phi1, xRotationMatrix, yRotationMatrix);
                Vector3f point2 = calculateTorusPoint(center, innerRadius, outerRadius, theta1, phi2, xRotationMatrix, yRotationMatrix);
                Vector3f point3 = calculateTorusPoint(center, innerRadius, outerRadius, theta2, phi1, xRotationMatrix, yRotationMatrix);
                Vector3f point4 = calculateTorusPoint(center, innerRadius, outerRadius, theta2, phi2, xRotationMatrix, yRotationMatrix);

                // Draw the quad strip for the torus
                vertexBuilder.vertex(positionMatrix, point1.x(), point1.y(), point1.z())
                        .color(r, g, b, alpha)
                        .normal(1, 0, 0)
                        .endVertex();
                vertexBuilder.vertex(positionMatrix, point2.x(), point2.y(), point2.z())
                        .color(r, g, b, alpha)
                        .normal(1, 0, 0)
                        .endVertex();
                vertexBuilder.vertex(positionMatrix, point4.x(), point4.y(), point4.z())
                        .color(r, g, b, alpha)
                        .normal(1, 0, 0)
                        .endVertex();
                vertexBuilder.vertex(positionMatrix, point3.x(), point3.y(), point3.z())
                        .color(r, g, b, alpha)
                        .normal(1, 0, 0)
                        .endVertex();
            }
        }
    }

    private static Vector3f calculateTorusPoint(Vector3f center, float innerRadius, float outerRadius, float theta, float phi, Matrix3f xRotationMatrix, Matrix3f yRotationMatrix) {
        float x = (float) ((outerRadius + innerRadius * Math.cos(phi)) * Math.cos(theta));
        float y = (float) (innerRadius * Math.sin(phi));
        float z = (float) ((outerRadius + innerRadius * Math.cos(phi)) * Math.sin(theta));

        // Apply the X and Y rotations
        Vector3f point = new Vector3f(x, y, z);
        point.mul(xRotationMatrix).mul(yRotationMatrix);

        // Translate to the center point
        return new Vector3f(center).add(point);
    }
}

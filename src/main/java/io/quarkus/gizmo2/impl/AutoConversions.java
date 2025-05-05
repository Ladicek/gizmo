package io.quarkus.gizmo2.impl;

import static io.smallrye.common.constraint.Assert.impossibleSwitchCase;
import static java.lang.constant.ConstantDescs.CD_Boolean;
import static java.lang.constant.ConstantDescs.CD_Byte;
import static java.lang.constant.ConstantDescs.CD_Character;
import static java.lang.constant.ConstantDescs.CD_Double;
import static java.lang.constant.ConstantDescs.CD_Float;
import static java.lang.constant.ConstantDescs.CD_Integer;
import static java.lang.constant.ConstantDescs.CD_Long;
import static java.lang.constant.ConstantDescs.CD_Short;
import static java.lang.constant.ConstantDescs.CD_Void;
import static java.lang.constant.ConstantDescs.CD_boolean;
import static java.lang.constant.ConstantDescs.CD_byte;
import static java.lang.constant.ConstantDescs.CD_char;
import static java.lang.constant.ConstantDescs.CD_double;
import static java.lang.constant.ConstantDescs.CD_float;
import static java.lang.constant.ConstantDescs.CD_int;
import static java.lang.constant.ConstantDescs.CD_long;
import static java.lang.constant.ConstantDescs.CD_short;
import static java.lang.constant.ConstantDescs.CD_void;

import java.lang.constant.ClassDesc;
import java.util.Map;
import java.util.Set;

import io.quarkus.gizmo2.TypeKind;

final class AutoConversions {
    static final Map<ClassDesc, ClassDesc> boxTypes = Map.of(
            CD_boolean, CD_Boolean,
            CD_byte, CD_Byte,
            CD_char, CD_Character,
            CD_short, CD_Short,
            CD_int, CD_Integer,
            CD_long, CD_Long,
            CD_float, CD_Float,
            CD_double, CD_Double,
            CD_void, CD_Void);

    static final Map<ClassDesc, ClassDesc> unboxTypes = Util.reverseMap(boxTypes);

    // TODO verify against JLS
    static final Map<ClassDesc, Set<ClassDesc>> wideningConversions = Map.of(
            CD_boolean, Set.of(),
            CD_byte, Set.of(CD_short, CD_int, CD_long, CD_float, CD_double),
            CD_char, Set.of(CD_int, CD_long, CD_float, CD_double),
            CD_short, Set.of(CD_int, CD_long, CD_float, CD_double),
            CD_int, Set.of(CD_long, CD_float, CD_double),
            CD_long, Set.of(CD_float, CD_double),
            CD_float, Set.of(CD_double),
            CD_double, Set.of(),
            CD_void, Set.of());

    static ClassDesc widerType(ClassDesc a, ClassDesc b) {
        if (a.isClassOrInterface()) {
            a = unboxTypes.getOrDefault(a, a);
        }
        if (b.isClassOrInterface()) {
            b = unboxTypes.getOrDefault(b, b);
        }
        if (a.isPrimitive() && b.isPrimitive()) {
            TypeKind aKind = TypeKind.from(a).asLoadable();
            TypeKind bKind = TypeKind.from(b).asLoadable();
            // TODO verify against JLS
            return switch (aKind) {
                case INT -> switch (bKind) {
                    case INT -> CD_int;
                    case LONG -> CD_long;
                    case FLOAT -> CD_float;
                    case DOUBLE -> CD_double;
                    default -> throw impossibleSwitchCase(bKind);
                };
                case LONG -> switch (bKind) {
                    case INT -> CD_long;
                    case LONG -> CD_long;
                    case FLOAT -> CD_float;
                    case DOUBLE -> CD_double;
                    default -> throw impossibleSwitchCase(bKind);
                };
                case FLOAT -> switch (bKind) {
                    case INT -> CD_float;
                    case LONG -> CD_float;
                    case FLOAT -> CD_float;
                    case DOUBLE -> CD_double;
                    default -> throw impossibleSwitchCase(bKind);
                };
                case DOUBLE -> switch (bKind) {
                    case INT -> CD_double;
                    case LONG -> CD_double;
                    case FLOAT -> CD_double;
                    case DOUBLE -> CD_double;
                    default -> throw impossibleSwitchCase(bKind);
                };
                default -> throw impossibleSwitchCase(TypeKind.from(a).asLoadable());
            };
        }
        throw new IllegalArgumentException("Expected both types to be primitive or primitive wrapper: " + a + ", " + b);
    }
}

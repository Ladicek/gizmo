package io.quarkus.gizmo2;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import io.quarkus.gizmo2.desc.ConstructorDesc;
import io.quarkus.gizmo2.desc.MethodDesc;

public class AutoConversionTest {
    @Test
    public void invoke_box_unbox() {
        TestClassMaker tcm = new TestClassMaker();
        Gizmo g = Gizmo.create(tcm);
        g.class_("io.quarkus.gizmo2.Invoke", cc -> {
            MethodDesc method = cc.staticMethod("method", mc -> {
                // static String method(int param1, Long param2) {
                //     return param1 + "_" + param2;
                // }
                ParamVar param1 = mc.parameter("param1", int.class);
                ParamVar param2 = mc.parameter("param2", Long.class);
                mc.returning(String.class);
                mc.body(bc -> {
                    bc.return_(bc.withNewStringBuilder().append(param1).append("_").append(param2).objToString());
                });
            });

            cc.staticMethod("test", mc -> {
                // static Object test() {
                //     return method(new Integer(13), 42L);
                // }
                mc.returning(Object.class); // always `String`
                mc.body(bc -> {
                    // unbox param1, box param2
                    bc.return_(bc.invokeStatic(method, bc.new_(Integer.class, Const.of(13)), Const.of(42L)));
                });
            });
        });
        assertEquals("13_42", tcm.staticMethod("test", Supplier.class).get());
    }

    @Test
    public void new_box_unbox() {
        TestClassMaker tcm = new TestClassMaker();
        Gizmo g = Gizmo.create(tcm);
        g.class_("io.quarkus.gizmo2.New", cc -> {
            ConstructorDesc ctor = cc.constructor(mc -> {
                mc.parameter("param1", int.class);
                mc.parameter("param2", Long.class);
                mc.body(bc -> {
                    bc.invokeSpecial(ConstructorDesc.of(Object.class), cc.this_());
                    bc.return_();
                });
            });

            cc.staticMethod("test", mc -> {
                // static Object test() {
                //     return new New(13, new Long(42L));
                // }
                mc.returning(Object.class);
                mc.body(bc -> {
                    // unbox param1, box param2
                    bc.return_(bc.new_(ctor, bc.new_(Integer.class, Const.of(13)), Const.of(42L)));
                });
            });
        });
        assertNotNull(tcm.staticMethod("test", Supplier.class).get());
    }

    @Test
    public void set_box_unbox() {
        TestClassMaker tcm = new TestClassMaker();
        Gizmo g = Gizmo.create(tcm);
        g.class_("io.quarkus.gizmo2.Set", cc -> {
            cc.staticMethod("test", mc -> {
                // static int test() {
                //     Integer local1 = 13;
                //     int local2 = new Integer(42);
                //     return local1 + local2;
                // }
                mc.returning(int.class);
                mc.body(bc -> {
                    LocalVar local1 = bc.declare("local1", Integer.class);
                    bc.set(local1, Const.of(13));

                    LocalVar local2 = bc.declare("local2", int.class);
                    bc.set(local2, bc.new_(Integer.class, Const.of(42)));

                    bc.return_(bc.add(local1, local2));
                });
            });
        });
        assertEquals(55, tcm.staticMethod("test", IntSupplier.class).getAsInt());
    }

    @Test
    public void newArray_unbox() {
        TestClassMaker tcm = new TestClassMaker();
        Gizmo g = Gizmo.create(tcm);
        g.class_("io.quarkus.gizmo2.NewArray", cc -> {
            cc.staticMethod("test", mc -> {
                // static Object test() {
                //     int[] array = new int[] { 13, new Integer(42), new Integer(13), 42 };
                //     return array;
                // }
                mc.returning(Object.class); // always `int[]`
                mc.body(bc -> {
                    Expr array = bc.newArray(int.class,
                            Const.of(13),
                            bc.new_(Integer.class, Const.of(42)),
                            bc.new_(Integer.class, Const.of(13)),
                            Const.of(42));
                    bc.return_(array);
                });
            });
        });
        assertArrayEquals(new int[] { 13, 42, 42, 13 }, (int[]) tcm.staticMethod("test", Supplier.class).get());
    }

    @Test
    public void newArray_box() {
        TestClassMaker tcm = new TestClassMaker();
        Gizmo g = Gizmo.create(tcm);
        g.class_("io.quarkus.gizmo2.NewArray", cc -> {
            cc.staticMethod("test", mc -> {
                // static Object test() {
                //     Integer[] array = new Integer[] { 13, new Integer(42), new Integer(13), 42 };
                //     return array;
                // }
                mc.returning(Object.class); // always `Integer[]`
                mc.body(bc -> {
                    Expr array = bc.newArray(Integer.class,
                            Const.of(13),
                            bc.new_(Integer.class, Const.of(42)),
                            bc.new_(Integer.class, Const.of(13)),
                            Const.of(42));
                    bc.return_(array);
                });
            });
        });
        assertArrayEquals(new Integer[] { 13, 42, 42, 13 }, (Integer[]) tcm.staticMethod("test", Supplier.class).get());
    }

    @Test
    public void plus_unbox() {
        TestClassMaker tcm = new TestClassMaker();
        Gizmo g = Gizmo.create(tcm);
        g.class_("io.quarkus.gizmo2.Plus", cc -> {
            cc.staticMethod("test", mc -> {
                // static int test() {
                //     return 3 + new Integer(5);
                // }
                mc.returning(int.class);
                mc.body(bc -> {
                    Expr a = Const.of(3);
                    Expr b = bc.new_(Integer.class, Const.of(5));
                    bc.return_(bc.add(a, b));
                });
            });
        });
        assertEquals(8, tcm.staticMethod("test", IntSupplier.class).getAsInt());
    }

    @Test
    public void plus_widen() {
        TestClassMaker tcm = new TestClassMaker();
        Gizmo g = Gizmo.create(tcm);
        g.class_("io.quarkus.gizmo2.Plus", cc -> {
            cc.staticMethod("test", mc -> {
                // static long test() {
                //     return 3 + 5L;
                // }
                mc.returning(long.class);
                mc.body(bc -> {
                    Expr a = Const.of(3);
                    Expr b = Const.of(5L);
                    bc.return_(bc.add(a, b));
                });
            });
        });
        assertEquals(8L, tcm.staticMethod("test", LongSupplier.class).getAsLong());
    }

    @Test
    public void neg_unbox() {
        TestClassMaker tcm = new TestClassMaker();
        Gizmo g = Gizmo.create(tcm);
        g.class_("io.quarkus.gizmo2.Neg", cc -> {
            cc.staticMethod("test", mc -> {
                // static int test() {
                //     return -new Integer(5);
                // }
                mc.returning(int.class);
                mc.body(bc -> {
                    bc.return_(bc.neg(bc.new_(Integer.class, Const.of(5))));
                });
            });
        });
        assertEquals(-5, tcm.staticMethod("test", IntSupplier.class).getAsInt());
    }
}

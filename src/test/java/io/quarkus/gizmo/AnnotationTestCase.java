package io.quarkus.gizmo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.ArrayType;
import org.jboss.jandex.DotName;
import org.jboss.jandex.PrimitiveType;
import org.jboss.jandex.Type;
import org.junit.Assert;
import org.junit.Test;

public class AnnotationTestCase {

    @Test
    public void testClassAnnotationWithString() throws ClassNotFoundException {
        TestClassLoader cl = new TestClassLoader(getClass().getClassLoader());
        try (ClassCreator creator = ClassCreator.builder().classOutput(cl).className("com.MyTest").build()) {
            addAnnotationWithString(creator);
        }

        MyAnnotation annotation = cl.loadClass("com.MyTest")
                .getAnnotation(MyAnnotation.class);
        Assert.assertEquals("test", annotation.value());
        Assert.assertEquals(MyEnum.YES, annotation.enumVal());
    }

    @Test
    public void testClassAnnotationWithStringArray() throws ClassNotFoundException {
        TestClassLoader cl = new TestClassLoader(getClass().getClassLoader());
        try (ClassCreator creator = ClassCreator.builder().classOutput(cl).className("com.MyTest").build()) {
            addAnnotationWithStringArray(creator);
        }

        MyArrayAnnotation annotation = cl.loadClass("com.MyTest")
                .getAnnotation(MyArrayAnnotation.class);
        Assert.assertArrayEquals(new String[] { "test" }, annotation.value());
    }

    @Test
    public void testClassAnnotationWithAnnotationValueArray() throws ClassNotFoundException {
        TestClassLoader cl = new TestClassLoader(getClass().getClassLoader());
        try (ClassCreator creator = ClassCreator.builder().classOutput(cl).className("com.MyTest").build()) {
            addAnnotationWithAnnotationValueArray(creator);
        }

        MyArrayAnnotation annotation = cl.loadClass("com.MyTest")
                .getAnnotation(MyArrayAnnotation.class);
        Assert.assertArrayEquals(new String[] { "test" }, annotation.value());
    }

    @Test
    public void testClassFullAnnotationUsingJandex() throws ClassNotFoundException {
        TestClassLoader cl = new TestClassLoader(getClass().getClassLoader());
        try (ClassCreator creator = ClassCreator.builder().classOutput(cl).className("com.MyTest").build()) {
            addFullAnnotationUsingJandex(creator);
        }

        MyFullAnnotation annotation = cl.loadClass("com.MyTest")
                .getAnnotation(MyFullAnnotation.class);
        verifyFullAnnotation(annotation);
    }

    @Test
    public void testClassFullAnnotationUsingNestedCreator() throws ClassNotFoundException {
        TestClassLoader cl = new TestClassLoader(getClass().getClassLoader());
        try (ClassCreator creator = ClassCreator.builder().classOutput(cl).className("com.MyTest").build()) {
            addFullAnnotationUsingNestedCreator(creator);
        }

        MyFullAnnotation annotation = cl.loadClass("com.MyTest")
                .getAnnotation(MyFullAnnotation.class);
        verifyFullAnnotation(annotation);
    }

    @Test
    public void testMethodAnnotationWithString() throws ClassNotFoundException, NoSuchMethodException {
        TestClassLoader cl = new TestClassLoader(getClass().getClassLoader());
        try (ClassCreator creator = ClassCreator.builder().classOutput(cl).className("com.MyTest").build()) {
            try (MethodCreator methodCreator = creator.getMethodCreator("test", void.class)) {
                addAnnotationWithString(methodCreator);
                methodCreator.returnValue(null);
            }
        }

        MyAnnotation annotation = cl.loadClass("com.MyTest")
                .getMethod("test")
                .getAnnotation(MyAnnotation.class);
        Assert.assertEquals("test", annotation.value());
        Assert.assertEquals(MyEnum.YES, annotation.enumVal());
    }

    @Test
    public void testMethodAnnotationWithEnum() throws ClassNotFoundException, NoSuchMethodException {
        TestClassLoader cl = new TestClassLoader(getClass().getClassLoader());
        try (ClassCreator creator = ClassCreator.builder().classOutput(cl).className("com.MyTest").build()) {
            try (MethodCreator methodCreator = creator.getMethodCreator("test", void.class)) {
                methodCreator.addAnnotation(AnnotationInstance.create(DotName.createSimple(MyAnnotation.class.getName()), null, new AnnotationValue[] {
                        AnnotationValue.createEnumValue("enumVal", DotName.createSimple("io.quarkus.gizmo.MyEnum"), "NO")
                } ));
                methodCreator.returnValue(null);
            }
        }

        MyAnnotation annotation = cl.loadClass("com.MyTest")
                .getMethod("test")
                .getAnnotation(MyAnnotation.class);
        Assert.assertEquals(MyEnum.NO, annotation.enumVal());
    }
    @Test
    public void testMethodAnnotationWithStringArray() throws ClassNotFoundException, NoSuchMethodException {
        TestClassLoader cl = new TestClassLoader(getClass().getClassLoader());
        try (ClassCreator creator = ClassCreator.builder().classOutput(cl).className("com.MyTest").build()) {
            try (MethodCreator methodCreator = creator.getMethodCreator("test", void.class)) {
                addAnnotationWithStringArray(methodCreator);
                methodCreator.returnValue(null);
            }
        }

        MyArrayAnnotation annotation = cl.loadClass("com.MyTest")
                .getMethod("test")
                .getAnnotation(MyArrayAnnotation.class);
        Assert.assertArrayEquals(new String[] { "test" }, annotation.value());
    }

    @Test
    public void testMethodAnnotationWithAnnotationValueArray() throws ClassNotFoundException, NoSuchMethodException {
        TestClassLoader cl = new TestClassLoader(getClass().getClassLoader());
        try (ClassCreator creator = ClassCreator.builder().classOutput(cl).className("com.MyTest").build()) {
            try (MethodCreator methodCreator = creator.getMethodCreator("test", void.class)) {
                addAnnotationWithAnnotationValueArray(methodCreator);
                methodCreator.returnValue(null);
            }
        }

        MyArrayAnnotation annotation = cl.loadClass("com.MyTest")
                .getMethod("test")
                .getAnnotation(MyArrayAnnotation.class);
        Assert.assertArrayEquals(new String[] { "test" }, annotation.value());
    }

    @Test
    public void testMethodFullAnnotationUsingJandex() throws ClassNotFoundException, NoSuchMethodException {
        TestClassLoader cl = new TestClassLoader(getClass().getClassLoader());
        try (ClassCreator creator = ClassCreator.builder().classOutput(cl).className("com.MyTest").build()) {
            try (MethodCreator methodCreator = creator.getMethodCreator("test", void.class)) {
                addFullAnnotationUsingJandex(methodCreator);
                methodCreator.returnValue(null);
            }
        }

        MyFullAnnotation annotation = cl.loadClass("com.MyTest")
                .getMethod("test")
                .getAnnotation(MyFullAnnotation.class);
        verifyFullAnnotation(annotation);
    }

    @Test
    public void testMethodFullAnnotationUsingNestedCreator() throws ClassNotFoundException, NoSuchMethodException {
        TestClassLoader cl = new TestClassLoader(getClass().getClassLoader());
        try (ClassCreator creator = ClassCreator.builder().classOutput(cl).className("com.MyTest").build()) {
            try (MethodCreator methodCreator = creator.getMethodCreator("test", void.class)) {
                addFullAnnotationUsingNestedCreator(methodCreator);
                methodCreator.returnValue(null);
            }
        }

        MyFullAnnotation annotation = cl.loadClass("com.MyTest")
                .getMethod("test")
                .getAnnotation(MyFullAnnotation.class);
        verifyFullAnnotation(annotation);
    }

    @Test
    public void testMethodParamAnnotationWithString() throws ClassNotFoundException, NoSuchMethodException {
        TestClassLoader cl = new TestClassLoader(getClass().getClassLoader());
        try (ClassCreator creator = ClassCreator.builder().classOutput(cl).className("com.MyTest").build()) {
            try (MethodCreator methodCreator = creator.getMethodCreator("test", void.class, String.class)) {
                addAnnotationWithString(methodCreator.getParameterAnnotations(0));
                methodCreator.returnValue(null);
            }
        }

        MyAnnotation annotation = cl.loadClass("com.MyTest")
                .getMethod("test", String.class)
                .getParameters()[0]
                .getAnnotation(MyAnnotation.class);
        Assert.assertEquals("test", annotation.value());
        Assert.assertEquals(MyEnum.YES, annotation.enumVal());
    }

    @Test
    public void testMethodParamAnnotationWithStringArray() throws ClassNotFoundException, NoSuchMethodException {
        TestClassLoader cl = new TestClassLoader(getClass().getClassLoader());
        try (ClassCreator creator = ClassCreator.builder().classOutput(cl).className("com.MyTest").build()) {
            try (MethodCreator methodCreator = creator.getMethodCreator("test", void.class, String.class)) {
                addAnnotationWithStringArray(methodCreator.getParameterAnnotations(0));
                methodCreator.returnValue(null);
            }
        }

        MyArrayAnnotation annotation = cl.loadClass("com.MyTest")
                .getMethod("test", String.class)
                .getParameters()[0]
                .getAnnotation(MyArrayAnnotation.class);
        Assert.assertArrayEquals(new String[] { "test" }, annotation.value());
    }

    @Test
    public void testMethodParamAnnotationWithAnnotationValueArray() throws ClassNotFoundException, NoSuchMethodException {
        TestClassLoader cl = new TestClassLoader(getClass().getClassLoader());
        try (ClassCreator creator = ClassCreator.builder().classOutput(cl).className("com.MyTest").build()) {
            try (MethodCreator methodCreator = creator.getMethodCreator("test", void.class, String.class)) {
                addAnnotationWithAnnotationValueArray(methodCreator.getParameterAnnotations(0));
                methodCreator.returnValue(null);
            }
        }

        MyArrayAnnotation annotation = cl.loadClass("com.MyTest")
                .getMethod("test", String.class)
                .getParameters()[0]
                .getAnnotation(MyArrayAnnotation.class);
        Assert.assertArrayEquals(new String[] { "test" }, annotation.value());
    }

    @Test
    public void testMethodParamFullAnnotationUsingJandex() throws ClassNotFoundException, NoSuchMethodException {
        TestClassLoader cl = new TestClassLoader(getClass().getClassLoader());
        try (ClassCreator creator = ClassCreator.builder().classOutput(cl).className("com.MyTest").build()) {
            try (MethodCreator methodCreator = creator.getMethodCreator("test", void.class, String.class)) {
                addFullAnnotationUsingJandex(methodCreator.getParameterAnnotations(0));
                methodCreator.returnValue(null);
            }
        }

        MyFullAnnotation annotation = cl.loadClass("com.MyTest")
                .getMethod("test", String.class)
                .getParameters()[0]
                .getAnnotation(MyFullAnnotation.class);
        verifyFullAnnotation(annotation);
    }

    @Test
    public void testMethodParamFullAnnotationUsingNestedCreator() throws ClassNotFoundException, NoSuchMethodException {
        TestClassLoader cl = new TestClassLoader(getClass().getClassLoader());
        try (ClassCreator creator = ClassCreator.builder().classOutput(cl).className("com.MyTest").build()) {
            try (MethodCreator methodCreator = creator.getMethodCreator("test", void.class, String.class)) {
                addFullAnnotationUsingNestedCreator(methodCreator.getParameterAnnotations(0));
                methodCreator.returnValue(null);
            }
        }

        MyFullAnnotation annotation = cl.loadClass("com.MyTest")
                .getMethod("test", String.class)
                .getParameters()[0]
                .getAnnotation(MyFullAnnotation.class);
        verifyFullAnnotation(annotation);
    }

    @Test
    public void testFieldAnnotationWithString() throws ClassNotFoundException, NoSuchFieldException {
        TestClassLoader cl = new TestClassLoader(getClass().getClassLoader());
        try (ClassCreator creator = ClassCreator.builder().classOutput(cl).className("com.MyTest").build()) {
            FieldCreator fieldCreator = creator.getFieldCreator("test", String.class);
            addAnnotationWithString(fieldCreator);
        }

        MyAnnotation annotation = cl.loadClass("com.MyTest")
                .getDeclaredField("test")
                .getAnnotation(MyAnnotation.class);
        Assert.assertEquals("test", annotation.value());
        Assert.assertEquals(MyEnum.YES, annotation.enumVal());
    }

    @Test
    public void testFieldAnnotationWithStringArray() throws ClassNotFoundException, NoSuchFieldException {
        TestClassLoader cl = new TestClassLoader(getClass().getClassLoader());
        try (ClassCreator creator = ClassCreator.builder().classOutput(cl).className("com.MyTest").build()) {
            FieldCreator fieldCreator = creator.getFieldCreator("test", String.class);
            addAnnotationWithStringArray(fieldCreator);
        }

        MyArrayAnnotation annotation = cl.loadClass("com.MyTest")
                .getDeclaredField("test")
                .getAnnotation(MyArrayAnnotation.class);
        Assert.assertArrayEquals(new String[] { "test" }, annotation.value());
    }

    @Test
    public void testFieldAnnotationWithAnnotationValueArray() throws ClassNotFoundException, NoSuchFieldException {
        TestClassLoader cl = new TestClassLoader(getClass().getClassLoader());
        try (ClassCreator creator = ClassCreator.builder().classOutput(cl).className("com.MyTest").build()) {
            FieldCreator fieldCreator = creator.getFieldCreator("test", String.class);
            addAnnotationWithStringArray(fieldCreator);
        }

        MyArrayAnnotation annotation = cl.loadClass("com.MyTest")
                .getDeclaredField("test")
                .getAnnotation(MyArrayAnnotation.class);
        Assert.assertArrayEquals(new String[] { "test" }, annotation.value());
    }

    @Test
    public void testFieldFullAnnotationUsingJandex() throws ClassNotFoundException, NoSuchFieldException {
        TestClassLoader cl = new TestClassLoader(getClass().getClassLoader());
        try (ClassCreator creator = ClassCreator.builder().classOutput(cl).className("com.MyTest").build()) {
            FieldCreator fieldCreator = creator.getFieldCreator("test", String.class);
            addFullAnnotationUsingJandex(fieldCreator);
        }

        MyFullAnnotation annotation = cl.loadClass("com.MyTest")
                .getDeclaredField("test")
                .getAnnotation(MyFullAnnotation.class);
        verifyFullAnnotation(annotation);
    }

    @Test
    public void testFieldFullAnnotationUsingNestedCreator() throws ClassNotFoundException, NoSuchFieldException {
        TestClassLoader cl = new TestClassLoader(getClass().getClassLoader());
        try (ClassCreator creator = ClassCreator.builder().classOutput(cl).className("com.MyTest").build()) {
            FieldCreator fieldCreator = creator.getFieldCreator("test", String.class);
            addFullAnnotationUsingNestedCreator(fieldCreator);
        }

        MyFullAnnotation annotation = cl.loadClass("com.MyTest")
                .getDeclaredField("test")
                .getAnnotation(MyFullAnnotation.class);
        verifyFullAnnotation(annotation);
    }

    private void addAnnotationWithString(AnnotatedElement element) {
        AnnotationCreator annotationCreator = element.addAnnotation(MyAnnotation.class);
        annotationCreator.addValue("value", "test");
        annotationCreator.addValue("enumVal", MyEnum.YES);
    }

    private void addAnnotationWithStringArray(AnnotatedElement element) {
        element.addAnnotation(MyArrayAnnotation.class).addValue("value", new String[] { "test" });
    }

    private void addAnnotationWithAnnotationValueArray(AnnotatedElement element) {
        AnnotationInstance annotation = AnnotationInstance.create(DotName.createSimple(MyArrayAnnotation.class.getName()), null,
                new AnnotationValue[] {
                        AnnotationValue.createArrayValue("value",
                                new AnnotationValue[] {
                                        AnnotationValue.createStringValue("value", "test")
                                }
                        )
                }
        );
        element.addAnnotation(annotation);
    }

    private void addFullAnnotationUsingJandex(AnnotatedElement element) {
        element.addAnnotation(AnnotationInstance.create(DotName.createSimple(MyFullAnnotation.class.getName()), null,
                new AnnotationValue[]{
                        AnnotationValue.createBooleanValue("bool", true),
                        AnnotationValue.createCharacterValue("ch", 'c'),
                        AnnotationValue.createByteValue("b", (byte) 42),
                        AnnotationValue.createShortValue("s", (short) 42),
                        AnnotationValue.createIntegerValue("i", 42),
                        AnnotationValue.createLongValue("l", 42L),
                        AnnotationValue.createFloatValue("f", 42.0F),
                        AnnotationValue.createDoubleValue("d", 42.0),
                        AnnotationValue.createStringValue("str", "str"),
                        AnnotationValue.createEnumValue("enumerated", DotName.createSimple(MyEnum.class.getName()), "YES"),
                        AnnotationValue.createClassValue("cls", Type.create(DotName.createSimple(MyInterface.class.getName()), Type.Kind.CLASS)),
                        AnnotationValue.createNestedAnnotationValue("nested", AnnotationInstance.create(DotName.createSimple(MyNestedAnnotation.class.getName()), null,
                                new AnnotationValue[]{
                                        AnnotationValue.createClassValue("cls", Type.create(DotName.createSimple(MyInterface.class.getName()), Type.Kind.CLASS)),
                                        AnnotationValue.createNestedAnnotationValue("innerNested", AnnotationInstance.create(DotName.createSimple(MyAnnotation.class.getName()), null,
                                                new AnnotationValue[]{
                                                        AnnotationValue.createStringValue("value", "nested"),
                                                        AnnotationValue.createEnumValue("enumVal", DotName.createSimple(MyEnum.class.getName()), "YES")
                                                })),
                                        AnnotationValue.createArrayValue("clsArray", new AnnotationValue[]{
                                                AnnotationValue.createClassValue("", Type.create(DotName.createSimple(MyInterface.class.getName()), Type.Kind.CLASS)),
                                                AnnotationValue.createClassValue("", ArrayType.create(PrimitiveType.BOOLEAN, 1)),
                                        }),
                                        AnnotationValue.createArrayValue("innerNestedArray", new AnnotationValue[]{
                                                AnnotationValue.createNestedAnnotationValue("", AnnotationInstance.create(DotName.createSimple(MyAnnotation.class.getName()), null,
                                                        new AnnotationValue[]{
                                                                AnnotationValue.createStringValue("value", "nested1"),
                                                                AnnotationValue.createEnumValue("enumVal", DotName.createSimple(MyEnum.class.getName()), "YES")
                                                        })),
                                                AnnotationValue.createNestedAnnotationValue("", AnnotationInstance.create(DotName.createSimple(MyAnnotation.class.getName()), null,
                                                        new AnnotationValue[]{
                                                                AnnotationValue.createStringValue("value", "nested2"),
                                                                AnnotationValue.createEnumValue("enumVal", DotName.createSimple(MyEnum.class.getName()), "NO")
                                                        })),
                                        }),
                                })),

                        AnnotationValue.createArrayValue("boolArray", new AnnotationValue[]{
                                AnnotationValue.createBooleanValue("", true),
                                AnnotationValue.createBooleanValue("", false),
                        }),
                        AnnotationValue.createArrayValue("chArray", new AnnotationValue[]{
                                AnnotationValue.createCharacterValue("", 'c'),
                                AnnotationValue.createCharacterValue("", 'd'),
                        }),
                        AnnotationValue.createArrayValue("bArray", new AnnotationValue[]{
                                AnnotationValue.createByteValue("", (byte) 42),
                                AnnotationValue.createByteValue("", (byte) 43),
                        }),
                        AnnotationValue.createArrayValue("sArray", new AnnotationValue[]{
                                AnnotationValue.createShortValue("", (short) 42),
                                AnnotationValue.createShortValue("", (short) 43),
                        }),
                        AnnotationValue.createArrayValue("iArray", new AnnotationValue[]{
                                AnnotationValue.createIntegerValue("", 42),
                                AnnotationValue.createIntegerValue("", 43),
                        }),
                        AnnotationValue.createArrayValue("lArray", new AnnotationValue[]{
                                AnnotationValue.createLongValue("", 42L),
                                AnnotationValue.createLongValue("", 43L),
                        }),
                        AnnotationValue.createArrayValue("fArray", new AnnotationValue[]{
                                AnnotationValue.createFloatValue("", 42.0F),
                                AnnotationValue.createFloatValue("", 43.0F),
                        }),
                        AnnotationValue.createArrayValue("dArray", new AnnotationValue[]{
                                AnnotationValue.createDoubleValue("", 42.0),
                                AnnotationValue.createDoubleValue("", 43.0),
                        }),
                        AnnotationValue.createArrayValue("strArray", new AnnotationValue[]{
                                AnnotationValue.createStringValue("", "foo"),
                                AnnotationValue.createStringValue("", "bar"),
                        }),
                        AnnotationValue.createArrayValue("enumeratedArray", new AnnotationValue[]{
                                AnnotationValue.createEnumValue("", DotName.createSimple(MyEnum.class.getName()), "YES"),
                                AnnotationValue.createEnumValue("", DotName.createSimple(MyEnum.class.getName()), "NO"),
                        }),
                        AnnotationValue.createArrayValue("clsArray", new AnnotationValue[]{
                                AnnotationValue.createClassValue("", Type.create(DotName.createSimple(MyInterface.class.getName()), Type.Kind.CLASS)),
                                AnnotationValue.createClassValue("", ArrayType.create(PrimitiveType.CHAR, 2)),
                        }),
                        AnnotationValue.createArrayValue("nestedArray", new AnnotationValue[]{
                                AnnotationValue.createNestedAnnotationValue("", AnnotationInstance.create(DotName.createSimple(MyNestedAnnotation.class.getName()), null,
                                        new AnnotationValue[]{
                                                AnnotationValue.createClassValue("cls", Type.create(DotName.createSimple(MyInterface.class.getName()), Type.Kind.CLASS)),
                                                AnnotationValue.createNestedAnnotationValue("innerNested", AnnotationInstance.create(DotName.createSimple(MyAnnotation.class.getName()), null,
                                                        new AnnotationValue[]{
                                                                AnnotationValue.createStringValue("value", "nested1"),
                                                                AnnotationValue.createEnumValue("enumVal", DotName.createSimple(MyEnum.class.getName()), "YES")
                                                        })),
                                                AnnotationValue.createArrayValue("clsArray", new AnnotationValue[]{
                                                        AnnotationValue.createClassValue("", Type.create(DotName.createSimple(MyInterface.class.getName()), Type.Kind.CLASS)),
                                                        AnnotationValue.createClassValue("", ArrayType.create(PrimitiveType.BOOLEAN, 1)),
                                                }),
                                                AnnotationValue.createArrayValue("innerNestedArray", new AnnotationValue[]{
                                                        AnnotationValue.createNestedAnnotationValue("", AnnotationInstance.create(DotName.createSimple(MyAnnotation.class.getName()), null,
                                                                new AnnotationValue[]{
                                                                        AnnotationValue.createStringValue("value", "nested11"),
                                                                        AnnotationValue.createEnumValue("enumVal", DotName.createSimple(MyEnum.class.getName()), "YES")
                                                                })),
                                                        AnnotationValue.createNestedAnnotationValue("", AnnotationInstance.create(DotName.createSimple(MyAnnotation.class.getName()), null,
                                                                new AnnotationValue[]{
                                                                        AnnotationValue.createStringValue("value", "nested12"),
                                                                        AnnotationValue.createEnumValue("enumVal", DotName.createSimple(MyEnum.class.getName()), "NO")
                                                                })),
                                                }),
                                        })),
                                AnnotationValue.createNestedAnnotationValue("", AnnotationInstance.create(DotName.createSimple(MyNestedAnnotation.class.getName()), null,
                                        new AnnotationValue[]{
                                                AnnotationValue.createClassValue("cls", Type.create(DotName.createSimple(MyInterface.class.getName()), Type.Kind.CLASS)),
                                                AnnotationValue.createNestedAnnotationValue("innerNested", AnnotationInstance.create(DotName.createSimple(MyAnnotation.class.getName()), null,
                                                        new AnnotationValue[]{
                                                                AnnotationValue.createStringValue("value", "nested2"),
                                                                AnnotationValue.createEnumValue("enumVal", DotName.createSimple(MyEnum.class.getName()), "YES")
                                                        })),
                                                AnnotationValue.createArrayValue("clsArray", new AnnotationValue[]{
                                                        AnnotationValue.createClassValue("", Type.create(DotName.createSimple(MyInterface.class.getName()), Type.Kind.CLASS)),
                                                        AnnotationValue.createClassValue("", ArrayType.create(PrimitiveType.BOOLEAN, 1)),
                                                }),
                                                AnnotationValue.createArrayValue("innerNestedArray", new AnnotationValue[]{
                                                        AnnotationValue.createNestedAnnotationValue("", AnnotationInstance.create(DotName.createSimple(MyAnnotation.class.getName()), null,
                                                                new AnnotationValue[]{
                                                                        AnnotationValue.createStringValue("value", "nested21"),
                                                                        AnnotationValue.createEnumValue("enumVal", DotName.createSimple(MyEnum.class.getName()), "YES")
                                                                })),
                                                        AnnotationValue.createNestedAnnotationValue("", AnnotationInstance.create(DotName.createSimple(MyAnnotation.class.getName()), null,
                                                                new AnnotationValue[]{
                                                                        AnnotationValue.createStringValue("value", "nested22"),
                                                                        AnnotationValue.createEnumValue("enumVal", DotName.createSimple(MyEnum.class.getName()), "NO")
                                                                })),
                                                }),
                                        })),
                        }),
                }
        ));
    }

    private void addFullAnnotationUsingNestedCreator(AnnotatedElement element) {
        AnnotationCreator creator = element.addAnnotation(MyFullAnnotation.class);

        creator.addValue("bool", true);
        creator.addValue("ch", 'c');
        creator.addValue("b", (byte) 42);
        creator.addValue("s", (short) 42);
        creator.addValue("i", 42);
        creator.addValue("l", 42L);
        creator.addValue("f", 42.0F);
        creator.addValue("d", 42.0);
        creator.addValue("str", "str");
        creator.addValue("enumerated", MyEnum.YES);
        creator.addValue("cls", MyInterface.class);
        creator.addValue("nested", AnnotationCreator.of(MyNestedAnnotation.class)
                .add("cls", MyInterface.class)
                .add("innerNested", AnnotationCreator.of(MyAnnotation.class)
                        .add("value", "nested")
                        .add("enumVal", MyEnum.YES))
                .add("clsArray", new Class[] {MyInterface.class, boolean[].class})
                .add("innerNestedArray", new AnnotationCreator[] {
                        AnnotationCreator.of(MyAnnotation.class)
                                .add("value", "nested1")
                                .add("enumVal", MyEnum.YES),
                        AnnotationCreator.of(MyAnnotation.class)
                                .add("value", "nested2")
                                .add("enumVal", MyEnum.NO)
                })
        );

        creator.addValue("boolArray", new boolean[] {true, false});
        creator.addValue("chArray", new char[] {'c', 'd'});
        creator.addValue("bArray", new byte[] {(byte) 42, (byte) 43});
        creator.addValue("sArray", new short[] {(short) 42, (short) 43});
        creator.addValue("iArray", new int[] {42, 43});
        creator.addValue("lArray", new long[] {42L, 43L});
        creator.addValue("fArray", new float[] {42.0F, 43.0F});
        creator.addValue("dArray", new double[] {42.0, 43.0});
        creator.addValue("strArray", new String[] {"foo", "bar"});
        creator.addValue("enumeratedArray", new MyEnum[] {MyEnum.YES, MyEnum.NO});
        creator.addValue("clsArray", new Class[] {MyInterface.class, char[][].class});
        creator.addValue("nestedArray", new AnnotationCreator[] {
                AnnotationCreator.of(MyNestedAnnotation.class)
                        .add("cls", MyInterface.class)
                        .add("innerNested", AnnotationCreator.of(MyAnnotation.class)
                                .add("value", "nested1")
                                .add("enumVal", MyEnum.YES))
                        .add("clsArray", new Class[] {MyInterface.class, boolean[].class})
                        .add("innerNestedArray", new AnnotationCreator[] {
                        AnnotationCreator.of(MyAnnotation.class)
                                .add("value", "nested11")
                                .add("enumVal", MyEnum.YES),
                        AnnotationCreator.of(MyAnnotation.class)
                                .add("value", "nested12")
                                .add("enumVal", MyEnum.NO)
                }),
                AnnotationCreator.of(MyNestedAnnotation.class)
                        .add("cls", MyInterface.class)
                        .add("innerNested", AnnotationCreator.of(MyAnnotation.class)
                                .add("value", "nested2")
                                .add("enumVal", MyEnum.YES))
                        .add("clsArray", new Class[] {MyInterface.class, boolean[].class})
                        .add("innerNestedArray", new AnnotationCreator[] {
                        AnnotationCreator.of(MyAnnotation.class)
                                .add("value", "nested21")
                                .add("enumVal", MyEnum.YES),
                        AnnotationCreator.of(MyAnnotation.class)
                                .add("value", "nested22")
                                .add("enumVal", MyEnum.NO)
                }),
        });
    }

    private void verifyFullAnnotation(MyFullAnnotation annotation) {
        Assert.assertEquals(true, annotation.bool());
        Assert.assertEquals('c', annotation.ch());
        Assert.assertEquals((byte) 42, annotation.b());
        Assert.assertEquals((short) 42, annotation.s());
        Assert.assertEquals(42, annotation.i());
        Assert.assertEquals(42L, annotation.l());
        Assert.assertEquals(42.0F, annotation.f(), 0.1f);
        Assert.assertEquals(42.0, annotation.d(), 0.1);
        Assert.assertEquals("str", annotation.str());
        Assert.assertEquals(MyEnum.YES, annotation.enumerated());
        Assert.assertEquals(MyInterface.class, annotation.cls());
        Assert.assertEquals(MyInterface.class, annotation.nested().cls());
        Assert.assertEquals("nested", annotation.nested().innerNested().value());
        Assert.assertEquals(MyEnum.YES, annotation.nested().innerNested().enumVal());
        Assert.assertArrayEquals(new Class[]{MyInterface.class, boolean[].class}, annotation.nested().clsArray());
        Assert.assertEquals("nested1", annotation.nested().innerNestedArray()[0].value());
        Assert.assertEquals(MyEnum.YES, annotation.nested().innerNestedArray()[0].enumVal());
        Assert.assertEquals("nested2", annotation.nested().innerNestedArray()[1].value());
        Assert.assertEquals(MyEnum.NO, annotation.nested().innerNestedArray()[1].enumVal());

        Assert.assertArrayEquals(new boolean[]{true, false}, annotation.boolArray());
        Assert.assertArrayEquals(new char[]{'c', 'd'}, annotation.chArray());
        Assert.assertArrayEquals(new byte[]{(byte) 42, (byte) 43}, annotation.bArray());
        Assert.assertArrayEquals(new short[]{(short) 42, (short) 43}, annotation.sArray());
        Assert.assertArrayEquals(new int[]{42, 43}, annotation.iArray());
        Assert.assertArrayEquals(new long[]{42L, 43L}, annotation.lArray());
        Assert.assertArrayEquals(new float[]{42.0F, 43.0F}, annotation.fArray(), 0.1f);
        Assert.assertArrayEquals(new double[]{42.0, 43.0}, annotation.dArray(), 0.1);
        Assert.assertArrayEquals(new String[]{"foo", "bar"}, annotation.strArray());
        Assert.assertArrayEquals(new MyEnum[]{MyEnum.YES, MyEnum.NO}, annotation.enumeratedArray());
        Assert.assertArrayEquals(new Class[]{MyInterface.class, char[][].class}, annotation.clsArray());
        Assert.assertEquals(MyInterface.class, annotation.nestedArray()[0].cls());
        Assert.assertEquals("nested1", annotation.nestedArray()[0].innerNested().value());
        Assert.assertEquals(MyEnum.YES, annotation.nestedArray()[0].innerNested().enumVal());
        Assert.assertArrayEquals(new Class[]{MyInterface.class, boolean[].class}, annotation.nestedArray()[0].clsArray());
        Assert.assertEquals("nested11", annotation.nestedArray()[0].innerNestedArray()[0].value());
        Assert.assertEquals(MyEnum.YES, annotation.nestedArray()[0].innerNestedArray()[0].enumVal());
        Assert.assertEquals("nested12", annotation.nestedArray()[0].innerNestedArray()[1].value());
        Assert.assertEquals(MyEnum.NO, annotation.nestedArray()[0].innerNestedArray()[1].enumVal());
        Assert.assertEquals(MyInterface.class, annotation.nestedArray()[1].cls());
        Assert.assertEquals("nested2", annotation.nestedArray()[1].innerNested().value());
        Assert.assertEquals(MyEnum.YES, annotation.nestedArray()[1].innerNested().enumVal());
        Assert.assertArrayEquals(new Class[]{MyInterface.class, boolean[].class}, annotation.nestedArray()[1].clsArray());
        Assert.assertEquals("nested21", annotation.nestedArray()[1].innerNestedArray()[0].value());
        Assert.assertEquals(MyEnum.YES, annotation.nestedArray()[1].innerNestedArray()[0].enumVal());
        Assert.assertEquals("nested22", annotation.nestedArray()[1].innerNestedArray()[1].value());
        Assert.assertEquals(MyEnum.NO, annotation.nestedArray()[1].innerNestedArray()[1].enumVal());
    }
}

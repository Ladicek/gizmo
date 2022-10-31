/*
 * Copyright 2018 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.quarkus.gizmo;

import org.junit.Test;

import java.util.function.Function;

import static org.junit.Assert.assertEquals;

public class LambdaTest {

    // see FunctionTestCase

    @Test
    public void testSimpleLambda() throws Exception {
        TestClassLoader cl = new TestClassLoader(getClass().getClassLoader());
        try (ClassCreator creator = ClassCreator.builder().classOutput(cl).className("com.MyTest").interfaces(MyInterface.class).build()) {
            MethodCreator method = creator.getMethodCreator("transform", String.class, String.class);

            //create a function that appends '-func' to its input
            FunctionCreator functionCreator = method.createLambda(Function.class);
            BytecodeCreator fbc = functionCreator.getBytecode();
            ResultHandle functionReturn = fbc.invokeVirtualMethod(MethodDescriptor.ofMethod(String.class, "concat", String.class, String.class), fbc.getMethodParam(0), fbc.load("-func"));
            fbc.returnValue(functionReturn);

            ResultHandle ret = method.invokeInterfaceMethod(MethodDescriptor.ofMethod(Function.class, "apply", Object.class, Object.class), functionCreator.getInstance(), method.getMethodParam(0));
            method.returnValue(ret);
        }

        Class<?> clazz = cl.loadClass("com.MyTest");
        MyInterface myInterface = (MyInterface) clazz.getDeclaredConstructor().newInstance();
        assertEquals("input-func", myInterface.transform("input"));
    }

}

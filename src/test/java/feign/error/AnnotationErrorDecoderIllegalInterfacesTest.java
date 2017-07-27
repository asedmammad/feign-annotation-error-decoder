package feign.error;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.Parameter;

import java.util.Arrays;

import java.util.Collection;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(Parameterized.class)
public class AnnotationErrorDecoderIllegalInterfacesTest {

    @Parameters(name = "{index}: When building interface ({0}) should return exception type ({1}) with message ({2})")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { IllegalTestClientInterfaceWithTooManyMappingsToSingleCode.class, IllegalStateException.class, "Status Code [404] has already been declared" },
            { IllegalTestClientInterfaceWithExceptionWithTooManyConstructors.class, IllegalStateException.class, "Too many constructors" },
            { IllegalTestClientInterfaceWithExceptionWithTooManyBodyParams.class, IllegalStateException.class, "Cannot have two parameters either without" },
            { IllegalTestClientInterfaceWithExceptionWithTooManyHeaderParams.class, IllegalStateException.class, "Cannot have two parameters tagged" },
            { IllegalTestClientInterfaceWithExceptionWithBadHeaderParams.class, IllegalStateException.class, "Cannot generate exception - check constructor" },
            { IllegalTestClientInterfaceWithExceptionWithBadHeaderObjectParam.class, IllegalStateException.class, "Response Header map must be of type Map" },
        });
    }

    @Parameter // first data value (0) is default
    public Class testInterface;

    @Parameter(1)
    public Class<? extends Exception> expectedExceptionClass;

    @Parameter(2)
    public String messageStart;

    @Test
    public void test() throws Exception {
        try {
            AnnotationErrorDecoder.builderFor(testInterface).build();
            fail("Should have thrown exception");
        }
        catch(Exception e) {
            assertThat(e.getClass()).isEqualTo(expectedExceptionClass);
            assertThat(e.getMessage()).startsWith(messageStart);
        }
    }

    interface IllegalTestClientInterfaceWithTooManyMappingsToSingleCode {
        @ErrorHandling(codeSpecific =
            {
                @ErrorCodes( codes = {404}, generate = Exception.class),
                @ErrorCodes( codes = {404}, generate = Exception.class)
            }
        )
        void method1Test();
    }

    interface IllegalTestClientInterfaceWithExceptionWithTooManyConstructors {
        @ErrorHandling(codeSpecific =
            {
                @ErrorCodes( codes = {404}, generate = BadException.class)
            }
        )
        void method1Test();

        class BadException extends Exception {

            @FeignExceptionConstructor
            public BadException(String body) {

            }
            @FeignExceptionConstructor
            public BadException(String body, @ResponseHeaders Map<String, Collection<String>> headers) {

            }
        }
    }

    interface IllegalTestClientInterfaceWithExceptionWithTooManyBodyParams {
        @ErrorHandling(codeSpecific =
            {
                @ErrorCodes( codes = {404}, generate = BadException.class)
            }
        )
        void method1Test();

        class BadException extends Exception {

            @FeignExceptionConstructor
            public BadException(String body, @ResponseBody String otherBody) {

            }
        }
    }

    interface IllegalTestClientInterfaceWithExceptionWithTooManyHeaderParams {

        @ErrorHandling(codeSpecific =
            {
                @ErrorCodes( codes = {404}, generate = BadException.class)
            }
        )
        void method1Test();

        class BadException extends Exception {

            @FeignExceptionConstructor
            public BadException(@ResponseHeaders Map<String,Collection<String>> headers1, @ResponseHeaders Map<String,Collection<String>> headers2) {

            }
        }
    }

    interface IllegalTestClientInterfaceWithExceptionWithBadHeaderParams {

        @ErrorHandling(codeSpecific =
            {
                @ErrorCodes( codes = {404}, generate = BadException.class)
            }
        )
        void method1Test();

        class BadException extends Exception {

            @FeignExceptionConstructor
            public BadException(@ResponseHeaders Map<Integer,String> headers1) {
                headers1.get(3);
            }
        }
    }

    interface IllegalTestClientInterfaceWithExceptionWithBadHeaderObjectParam {

        @ErrorHandling(codeSpecific =
            {
                @ErrorCodes( codes = {404}, generate = BadException.class)
            }
        )
        void method1Test();

        class BadException extends Exception {

            @FeignExceptionConstructor
            public BadException(@ResponseHeaders TestPojo headers1) {
            }
        }
    }



}
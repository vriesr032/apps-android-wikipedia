package org.wikipedia.json;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.ParameterizedRobolectricTestRunner.Parameters;
import org.wikipedia.page.Namespace;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.wikipedia.json.GsonMarshaller.marshal;
import static org.wikipedia.json.GsonUnmarshaller.unmarshal;

@RunWith(ParameterizedRobolectricTestRunner.class) public class NamespaceTypeAdapterTest {
    @Parameters(name = "{0}") public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {{DeferredParam.NULL}, {DeferredParam.SPECIAL},
                {DeferredParam.MAIN}, {DeferredParam.TALK}});
    }

    @Nullable private final Namespace namespace;

    public NamespaceTypeAdapterTest(@NonNull DeferredParam param) {
        this.namespace = param.val();
    }

    @Test public void testWriteRead() {
        Namespace result = unmarshal(Namespace.class, marshal(namespace));
        assertThat(result, is(namespace));
    }

    @Test public void testReadOldData() {
        String marshaledStr = namespace == null ? "null" : "\"" + namespace.name() + "\"";
        Namespace ns = unmarshal(Namespace.class, marshaledStr);
        assertThat(ns, is(namespace));
    }

    // SparseArray is a Roboelectric mocked class which is unavailable at static time; defer
    // evaluation until TestRunner is executed
    private enum DeferredParam {
        NULL() {
            @Nullable @Override
            Namespace val() {
                return null;
            }
        },
        SPECIAL() {
            @NonNull @Override Namespace val() {
                return Namespace.SPECIAL;
            }
        },
        MAIN() {
            @NonNull @Override Namespace val() {
                return Namespace.MAIN;
            }
        },
        TALK() {
            @NonNull @Override Namespace val() {
                return Namespace.TALK;
            }
        };

        @Nullable abstract Namespace val();
    }
}

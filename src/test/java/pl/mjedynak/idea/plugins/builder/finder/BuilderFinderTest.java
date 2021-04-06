package pl.mjedynak.idea.plugins.builder.finder;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BuilderFinderTest {

    private static final String CLASS_NAME = "SomeClass";
    private static final String BUILDER_NAME = CLASS_NAME + BuilderFinder.SEARCH_PATTERN;

    @InjectMocks private BuilderFinder builderFinder;
    @Mock private ClassFinder classFinder;
    @Mock private PsiClass psiClass;
    @Mock private PsiClass builderClass;
    @Mock private Project project;

    @Before
    public void setUp() {
        given(psiClass.isEnum()).willReturn(false);
        given(psiClass.isInterface()).willReturn(false);
        given(psiClass.isAnnotationType()).willReturn(false);
        given(psiClass.getProject()).willReturn(project);
        given(psiClass.getName()).willReturn(CLASS_NAME);
        given(psiClass.getInnerClasses()).willReturn(new PsiClass[0]);

        given(builderClass.getName()).willReturn(BUILDER_NAME);
        given(builderClass.getProject()).willReturn(project);
    }

    @Test
    public void shouldNotFindBuilderForEnum() {
        // given
        given(psiClass.isEnum()).willReturn(true);

        // when
        PsiClass result = builderFinder.findBuilderForClass(psiClass);

        // then
        assertThat(result).isNull();
    }

    @Test
    public void shouldNotFindBuilderForInterface() {
        // given
        given(psiClass.isAnnotationType()).willReturn(true);

        // when
        PsiClass result = builderFinder.findBuilderForClass(psiClass);

        // then
        assertThat(result).isNull();
    }

    @Test
    public void shouldNotFindBuilderForAnnotationType() {
        // given
        given(psiClass.isAnnotationType()).willReturn(true);

        // when
        PsiClass result = builderFinder.findBuilderForClass(psiClass);

        // then
        assertThat(result).isNull();
    }

    @Test
    public void shouldNotFindBuilderForClassWhenClassFounderReturnsNull() {
        // given
        given(classFinder.findClass(CLASS_NAME, project)).willReturn(null);

        // when
        PsiClass result = builderFinder.findBuilderForClass(psiClass);

        // then
        assertThat(result).isNull();
    }

    @Test
    public void shouldFindBuilderForClassWhenBuilderWithTheExactClassNameIsPresent() {
        // given

        PsiClass builderClass = mock(PsiClass.class);
        given(builderClass.getName()).willReturn(BUILDER_NAME);

        given(classFinder.findClass(BUILDER_NAME, project)).willReturn(builderClass);

        // when
        PsiClass result = builderFinder.findBuilderForClass(psiClass);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(BUILDER_NAME);
    }

    @Test
    public void shouldFindInnerBuilder() {
        // given
        PsiClass innerClass = mock(PsiClass.class);
        PsiClass[] innerClasses = {innerClass};
        given(innerClass.getName()).willReturn(BuilderFinder.SEARCH_PATTERN);
        given(psiClass.getInnerClasses()).willReturn(innerClasses);

        // when
        PsiClass result = builderFinder.findBuilderForClass(psiClass);

        // then
        assertThat(result).isEqualTo(innerClass);
    }

    @Test
    public void shouldNotFindInnerBuilderWhenInnerClassNameDoesNotMatchPattern() {
        // given
        PsiClass innerClass = mock(PsiClass.class);
        PsiClass[] innerClasses = {innerClass};
        given(innerClass.getName()).willReturn("SomeInnerClass");
        given(psiClass.getInnerClasses()).willReturn(innerClasses);

        // when
        PsiClass result = builderFinder.findBuilderForClass(psiClass);

        // then
        assertThat(result).isNull();
    }

    @Test
    public void shouldNotFindClassForEnum() {
        // given
        given(psiClass.isEnum()).willReturn(true);

        // when
        PsiClass result = builderFinder.findClassForBuilder(psiClass);

        // then
        assertThat(result).isNull();
    }

    @Test
    public void shouldNotFindClassForInterface() {
        // given
        given(psiClass.isAnnotationType()).willReturn(true);

        // when
        PsiClass result = builderFinder.findClassForBuilder(psiClass);

        // then
        assertThat(result).isNull();
    }

    @Test
    public void shouldNotFindClassForAnnotationType() {
        // given
        given(psiClass.isAnnotationType()).willReturn(true);

        // when
        PsiClass result = builderFinder.findClassForBuilder(psiClass);

        // then
        assertThat(result).isNull();
    }

    @Test
    public void shouldNotFindClassForBuilderWhenClassFounderReturnsNull() {
        // given
        given(classFinder.findClass(BUILDER_NAME, project)).willReturn(null);

        // when
        PsiClass result = builderFinder.findClassForBuilder(builderClass);

        // then
        assertThat(result).isNull();
        verify(classFinder).findClass(CLASS_NAME, project);
    }

    @Test
    public void shouldFindClassForBuilderWhenClassWithTheExactBuildersNameIsPresent() {
        // given
        given(classFinder.findClass(CLASS_NAME, project)).willReturn(psiClass);

        // when
        PsiClass result = builderFinder.findClassForBuilder(psiClass);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(CLASS_NAME);
    }

}
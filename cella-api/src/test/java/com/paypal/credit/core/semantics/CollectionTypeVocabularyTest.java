package com.paypal.credit.core.semantics;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

/**

/**
 *
 */
public class CollectionTypeVocabularyTest 
{

	/**
	 * Test method for
	 */
	@Test
	public void testGetSimpleName()
	{
		assertEquals("List", CollectionType.LIST.getSimpleName() );
		assertEquals("Set", CollectionType.SET.getSimpleName() );
		assertEquals("Map", CollectionType.MAP.getSimpleName() );
	}

	/**
	 * Test method for
	 */
    @Test
	public void testFindByObjectSuffix()
	{
		assertEquals(CollectionType.findByObjectSuffix("List"), CollectionType.LIST );
		assertEquals(CollectionType.findByObjectSuffix("Set"), CollectionType.SET );
		assertEquals(CollectionType.findByObjectSuffix("Map"), CollectionType.MAP );
	}
}

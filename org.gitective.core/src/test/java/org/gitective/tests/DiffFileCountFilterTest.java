/*
 * Copyright (c) 2011 Kevin Sawicki <kevinsawicki@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */
package org.gitective.tests;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.CommitFinder;
import org.gitective.core.filter.commit.DiffFileCountFilter;
import org.junit.Test;

/**
 * Unit tests of {@link DiffFileCountFilter}
 */
public class DiffFileCountFilterTest extends GitTestCase {

	/**
	 * Validate default state of filter
	 */
	@Test
	public void defaultState() {
		DiffFileCountFilter filter = new DiffFileCountFilter();
		assertEquals(0, filter.getAdded());
		assertEquals(0, filter.getDeleted());
		assertEquals(0, filter.getDeleted());
	}

	/**
	 * Clone a {@link DiffFileCountFilter}
	 */
	@Test
	public void cloneFilter() {
		DiffFileCountFilter filter = new DiffFileCountFilter();
		RevFilter clone = filter.clone();
		assertNotNull(clone);
		assertTrue(clone instanceof DiffFileCountFilter);
		assertNotSame(filter, clone);
	}

	/**
	 * Add single file
	 *
	 * @throws Exception
	 */
	@Test
	public void singleAdd() throws Exception {
		add("test.txt", "content");
		DiffFileCountFilter filter = new DiffFileCountFilter();
		new CommitFinder(testRepo).setFilter(filter).find();
		assertEquals(1, filter.getAdded());
		assertEquals(0, filter.getEdited());
		assertEquals(0, filter.getDeleted());
		filter.reset();
		assertEquals(0, filter.getAdded());
	}

	/**
	 * Add multiple files
	 *
	 * @throws Exception
	 */
	@Test
	public void multipleAdd() throws Exception {
		add("test.txt", "content");
		add("test2.txt", "more content");
		DiffFileCountFilter filter = new DiffFileCountFilter();
		new CommitFinder(testRepo).setFilter(filter).find();
		assertEquals(2, filter.getAdded());
		assertEquals(0, filter.getEdited());
		assertEquals(0, filter.getDeleted());
	}

	/**
	 * Add, edit, and delete the same file
	 *
	 * @throws Exception
	 */
	@Test
	public void addEditDelete() throws Exception {
		add("test.txt", "content");
		add("test.txt", "content2");
		delete("test.txt");
		DiffFileCountFilter filter = new DiffFileCountFilter();
		new CommitFinder(testRepo).setFilter(filter).find();
		assertEquals(1, filter.getAdded());
		assertEquals(1, filter.getEdited());
		assertEquals(1, filter.getDeleted());
	}

	/**
	 * Verify single file edit using a
	 * {@link CommitFinder#findBetween(org.eclipse.jgit.lib.ObjectId, org.eclipse.jgit.lib.ObjectId)}
	 * call
	 *
	 * @throws Exception
	 */
	@Test
	public void singleEditFindBetween() throws Exception {
		RevCommit commit1 = add("test.txt", "content");
		RevCommit commit2 = add("test.txt", "content2");
		DiffFileCountFilter filter = new DiffFileCountFilter();
		new CommitFinder(testRepo).setFilter(filter).findBetween(commit2,
				commit1);
		assertEquals(0, filter.getAdded());
		assertEquals(1, filter.getEdited());
		assertEquals(0, filter.getDeleted());
	}
}

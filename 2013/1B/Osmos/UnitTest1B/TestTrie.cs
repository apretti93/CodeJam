﻿using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Trie;
namespace UnitTest1B
{
    [TestClass]
    public class TestTrie
    {
        [TestMethod]
        public void testMatches()
        {
            Dictionary dict = new Dictionary();
            dict.words.Add("abcdef");
            dict.words.Add("bbcdezg");
            dict.words.Add("acc");
            dict.words.Add("bac");
            TrieNode root = TrieNode.createRootNode(dict);

            List<TrieNode.WordMatch> matches;
            root.parseText("abcdefghi", null, out matches);

            Assert.AreEqual(3, matches.Count);
            Assert.AreEqual(dict.words[2], matches[0].Word);
            Assert.AreEqual(1, matches[0][0]);
            Assert.AreEqual(1, matches[0][0,Trie.TrieNode.WordMatch.LeftOrRight.right]);

            Assert.AreEqual(dict.words[0], matches[1].Word);
            Assert.AreEqual(0, matches[1].changeCount());

            Assert.AreEqual(dict.words[1], matches[2].Word);
            Assert.AreEqual(0, matches[2][0]);
            Assert.AreEqual(6, matches[2][0, Trie.TrieNode.WordMatch.LeftOrRight.right]);

            Assert.AreEqual(5, matches[2][1]);
            Assert.AreEqual(1, matches[2][1, Trie.TrieNode.WordMatch.LeftOrRight.right]);
        }


        [TestMethod]
        public void testBasicMatches()
        {
            Dictionary dict = new Dictionary();
            dict.words.Add("abc");
            dict.words.Add("bccd");
            dict.words.Add("abcc");
            dict.words.Add("bab");
            TrieNode root = TrieNode.createRootNode(dict);

            List<TrieNode.WordMatch> matches;
            root.parseText("abccd",null, out matches);

            Assert.AreEqual(2, matches.Count);
            Assert.AreEqual(dict.words[0], matches[0].Word);
            Assert.AreEqual(dict.words[2], matches[1].Word);
            

        }
        [TestMethod]
        public void testAddWord()
        {
            Dictionary dict = new Dictionary();
            dict.words.Add("abc");
            dict.words.Add("acc");
            TrieNode root = TrieNode.createRootNode(dict);
            
            Assert.AreEqual(1, root.childrenList.Count);
            TrieNode nodeA = root.children[0];
            Assert.AreSame(root.childrenList[0].Item2, nodeA);

            Assert.AreEqual(2, nodeA.childrenList.Count);

            TrieNode nodeB = nodeA.children[1];
            TrieNode nodeC = nodeA.children[2];

            Assert.AreEqual(1, nodeB.childrenList.Count);
            Assert.AreEqual(1, nodeC.childrenList.Count);

        }
    }
}

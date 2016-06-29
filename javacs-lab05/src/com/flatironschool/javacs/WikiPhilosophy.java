package com.flatironschool.javacs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import org.jsoup.select.Elements;

public class WikiPhilosophy {
	
	final static WikiFetcher wf = new WikiFetcher();
	
	/**
	 * Tests a conjecture about Wikipedia and Philosophy.
	 * 
	 * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
	 * 
	 * 1. Clicking on the first non-parenthesized, non-italicized link
     * 2. Ignoring external links, links to the current page, or red links
     * 3. Stopping when reaching "Philosophy", a page with no links or a page
     *    that does not exist, or when a loop occurs
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
        // some example code to get you started

		String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";
		List<String> visited = new ArrayList<String>();
		visited.add(url);
		while(true)
		{
			url = fetch(url);
			if(visited.contains(url))
			{
				System.out.println("In a loop");
				break;
			}
			else if(url.equals("https://en.wikipedia.org/wiki/Philosophy"))
			{
				System.out.println("Reached Philosophy!!");
				break;
			} 
			else{
				System.out.println(url);
				visited.add(url);
			}	
		}
	}

	public static String fetch(String url) throws IOException
	{
		String link;
		Elements paragraphs = wf.fetchWikipedia(url);
		Stack<Character> stack_par =new Stack<Character>();
		for(Element paragraph: paragraphs)
		{
			Iterable<Node> iterator = new WikiNodeIterable(paragraph);
			for(Node node: iterator)
			{
				if(node instanceof Element && stack_par.isEmpty())
				{
					link = node.attr("href");
					if(isValid((Element)node,url))
					{
						return "https://en.wikipedia.org"+link;
					}
				}
				else if(node instanceof TextNode)
				{
					stack_par = paren(stack_par,((TextNode)node).text());
				}
			}
		}
		return "";
	}

	public static Stack<Character> paren(Stack<Character> stack,String text)
	{
		for(int i=0;i<text.length();i++)
		{
			char ch = text.charAt(i);
			if(ch=='('||ch=='[')
			{
				stack.push(ch);
			}
			else if( ch==')')
			{
				if(stack.peek()=='(')
				{
					stack.pop();
				}
			}
			else if(ch==']')
			{
				if(stack.peek()=='[')
				{
					stack.pop();
				}
			}
		}
		return stack;
	}

	public static boolean isValid(Element element,String url)
	{
		return checkItalics(element)&&
				checkUpperCase(element)&&
				checkParens(element)&&
				checkIfMatchesCurrentURL(element,url);
	}

	public static boolean checkItalics(Element element)
	{
		String tag = element.tag().toString();
		if(tag.equals("i")||tag.equals("em"))
		{
			return false;
		}
		return true;	
	}

	public static boolean checkUpperCase(Element element)
	{
		String text = element.text().toString();
		int last_index = text.length()-1;
		if((text.charAt(0)=='('&&text.charAt(last_index)==')')||(text.charAt(0)=='('&&text.charAt(last_index)==')'))
		{
			return false;
		}
		return true;	
	}

	public static boolean checkIfMatchesCurrentURL(Element element,String url)
	{
		String curr_url = element.attr("href");
		if(curr_url.length()==0)return false;
		curr_url = "https://"+curr_url;
		if(curr_url.equals(url))
		{
			return false;
		}
		return true;	
	}

	public static boolean checkParens(Element element)
	{
		String text = element.text().toString();
		char start = text.charAt(0);
		char end = text.charAt(text.length()-1);
		if(start=='['&&end==']') return false;
		if(start=='('&&end==')') return false;
		return true;
	}


}

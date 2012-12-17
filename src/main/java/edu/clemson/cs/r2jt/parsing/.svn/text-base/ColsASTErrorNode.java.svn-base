package edu.clemson.cs.r2jt.parsing;

import org.antlr.runtime.MissingTokenException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.UnwantedTokenException;
import org.antlr.runtime.tree.CommonErrorNode;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeNodeStream;


public class ColsASTErrorNode extends ColsAST {
	org.antlr.runtime.tree.CommonErrorNode delegate;
	
	public ColsASTErrorNode(TokenStream input, Token start, Token stop,
								RecognitionException e){
		delegate = new CommonErrorNode(input, start, stop, e);
	}
	public boolean isNil(){return delegate.isNil();};
	
	public int getType(){return delegate.getType();};
	
	public String getText(){
		return delegate.getText();
	};
	
	public String toString(){
		return delegate.toString();
	};
         /*public IntStream input;
        public Token start;
         public Token stop;
         public RecognitionException trappedException;
 
        public ColsASTErrorNode(TokenStream input, Token start, Token stop,
                                                   RecognitionException e)
        {
                 //System.out.println("start: "+start+", stop: "+stop);
                 if ( stop==null ||
                          (stop.getTokenIndex() < start.getTokenIndex() &&
                           stop.getType()!=Token.EOF) )
                 {
                         // sometimes resync does not consume a token (when LT(1) is
                        // in follow set.  So, stop will be 1 to left to start. adjust.
                         // Also handle case where start is the first token and no token
                        // is consumed during recovery; LT(-1) will return null.
                         stop = start;
                 }
                this.input = input;
                this.start = start;
                 this.stop = stop;
                 this.trappedException = e;
         }
 
         public boolean isNil() {
                 return false;
         }
 
         public int getType() {
                 return Token.INVALID_TOKEN_TYPE;
         }
 
         public String getText() {
                 String badText = null;
                 if ( start instanceof Token ) {
                         int i = ((Token)start).getTokenIndex();
                         int j = ((Token)stop).getTokenIndex();
                        if ( ((Token)stop).getType() == Token.EOF ) {
                                 j = ((TokenStream)input).size();
                         }
                         badText = ((TokenStream)input).toString(i, j);
                 }
                 else if ( start instanceof Tree ) {
                         badText = ((TreeNodeStream)input).toString(start, stop);
                }
                 else {
                         // people should subclass if they alter the tree type so this
                         // next one is for sure correct.
                         badText = "<unknown>";
                }
                 return badText;
         }
 
         public String toString() {
                 if ( trappedException instanceof MissingTokenException ) {
                         return "<missing type: "+
                                   ((MissingTokenException)trappedException).getMissingType()+
                                    ">";
                 }
                 else if ( trappedException instanceof UnwantedTokenException ) {
                         return "<extraneous: "+
                                    ((UnwantedTokenException)trappedException).getUnexpectedToken()+
                                    ", resync="+getText()+">";
                 }
                 else if ( trappedException instanceof MismatchedTokenException ) {
                        return "<mismatched token: "+trappedException.token+", resync="+getText()+">";
                 }
                 else if ( trappedException instanceof NoViableAltException ) {
                         return "<unexpected: "+trappedException.token+
                                    ", resync="+getText()+">";
                }
                 return "<error: "+getText()+">";
         }*/
}


package jscl.mathml;

import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.DocumentTypeImpl;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.dom.TextImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;

public class MathML {

    private static Transformer xhtml;

    @NotNull
    private final Node node;

    private MathML(@NotNull Node node) {
        this.node = node;
    }

    @Nullable
    public Document getDocument() {
        return node instanceof CoreDocumentImpl ? (Document) node : node.getOwnerDocument();
    }

    public MathML(@NotNull String qualifiedName,
                  @NotNull String publicId,
                  @NotNull String systemId) {
        final CoreDocumentImpl document = new CoreDocumentImpl();
        document.setXmlEncoding("utf-8");
        document.appendChild(new DocumentTypeImpl(document, qualifiedName, publicId, systemId));

        this.node = document;
    }

    @NotNull
    public MathML newElement(@NotNull String name) {
        final CoreDocumentImpl document = (CoreDocumentImpl) getDocument();
        return new MathML(new ElementImpl(document, name));
    }

    public void setAttribute(@NotNull String name,
                             @NotNull String value) {
        ((Element) node).setAttribute(name, value);
    }

    @NotNull
    public MathML newText(@NotNull String data) {
        CoreDocumentImpl document = (CoreDocumentImpl) getDocument();
        return new MathML(new TextImpl(document, data));
    }

    public void appendChild(@NotNull MathML math) {
        node.appendChild(math.node);
    }

    public String toString() {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            getTransformer().transform(new DOMSource(node), new StreamResult(os));
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
        String s = os.toString();
        return s.substring(s.indexOf(">") + 1);
    }

    @NotNull
    private static synchronized Transformer getTransformer() throws TransformerException {
        return xhtml == null ? xhtml = TransformerFactory.newInstance().newTransformer() : xhtml;
    }
}

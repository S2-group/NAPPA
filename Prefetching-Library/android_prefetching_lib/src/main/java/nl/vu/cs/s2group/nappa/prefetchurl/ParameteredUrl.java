package nl.vu.cs.s2group.nappa.prefetchurl;

import androidx.annotation.NonNull;

import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nl.vu.cs.s2group.nappa.room.data.ActivityExtraData;
import nl.vu.cs.s2group.nappa.room.data.UrlCandidate;
import nl.vu.cs.s2group.nappa.room.data.UrlCandidateParts;

// TODO Consider moving class to room package
//  This class is returned as a model in select operations in the DAO class UrlCandidateDao.
//  As such, it makes more sense to define it as a model class.
//  This class could be:
//  1. Moved as it is to the room package
//  2. Refactored to split model and business logic
//  3. Used as a DatabaseView
/**
 * Represents an individual URL composed of both static components and parameter components.  The
 * components are stored as a linked list of {@linkplain UrlParameter} objects.  This is the static
 * representation of the {@link UrlCandidate} and {@link UrlCandidateParts } database entities.
 */
public class ParameteredUrl {

    private List<UrlParameter> urlParameterList = new LinkedList<>();

    public enum TYPES {
        STATIC, PARAMETER
    }

    /**
     * Translates a {@linkplain TYPES} Enum from its numerical value back to its ENUM Value Type
     * @param id
     * @return
     */
    public static TYPES getTYPESFromId(int id) {
        if (id==0)
            return TYPES.STATIC;
        return TYPES.PARAMETER;
    }

    /**
     * Translates a {@linkplain TYPES} Enum from its enum value type back to its numerical value
     * @param types
     * @return
     */
    public static int getIdFromTypes(TYPES types) {
        if (types == TYPES.STATIC)
            return 0;
        return 1;
    }

    public ParameteredUrl() {
    }

    /**
     * From a list of diff operations,  add parameters to  the urlParameterList
     * @param diffs List of diff operations, which where a diff represents an insertion, a deletion, or
     *              equals (keep) operation.  This diff is performed between a URL and a {@link ActivityExtraData#value}
     *              out of an extra key value pair to determine where an extra value is contained in an URL
     * @param inverse Whether an EQUAL or INSERT operation corresponds to a STATIC Type or a PARAMETER
     *                type or viceversa.  Inverse = TRUE is used when Inserting Static URL aspects
     *                identified from a full URL with parameters
     */
    public ParameteredUrl(LinkedList<DiffMatchPatch.Diff> diffs, boolean inverse) {
        int count = 0;

        DiffMatchPatch.Operation op1 = DiffMatchPatch.Operation.EQUAL;
        DiffMatchPatch.Operation op2 = DiffMatchPatch.Operation.INSERT;
        if (inverse) {
            op1 = op2;
            op2 = DiffMatchPatch.Operation.EQUAL;
        }

        for (DiffMatchPatch.Diff diff : diffs) {
            //System.out.println(diff);

            if (diff.operation == op1) {
                this.addParameter(count, ParameteredUrl.TYPES.STATIC, diff.text);
                count++;
            } else if (diff.operation == op2){
                this.addParameter(count, ParameteredUrl.TYPES.PARAMETER, diff.text);
                count++;
            }

        }
    }

    /**
     * Creates an UrlParameter object and adds it to the list
     * @param order
     * @param type
     * @param urlPiece
     */
    public void addParameter(int order, TYPES type, String urlPiece) {
        urlParameterList.add(new UrlParameter(order, type, urlPiece));
    }

    public List<UrlParameter> getUrlParameterList() {
        Collections.sort(urlParameterList);
        return urlParameterList;
    }

    public String fillParams(Map<String, String> paramMap) {
        StringBuilder sb = new StringBuilder();
        Collections.sort(urlParameterList);
        for (UrlParameter parameter : urlParameterList) {
            if (parameter.type == TYPES.PARAMETER) {
                String value = paramMap.get(parameter.urlPiece);
                if (value != null) {
                    sb.append(value);
                }
            } else {
                sb.append(parameter.urlPiece);
            }
        }
        return sb.toString();
    }

    public List<String> getParamKeys() {
        LinkedList<String> ret = new LinkedList<>();
        for (UrlParameter parameter : urlParameterList) {
            if (parameter.type == TYPES.PARAMETER) {
                ret.add(parameter.urlPiece);
            }
        }
        return ret;
    }

    /**
     * Defines equality of two ParameteredUrl objects as being the same object type and having
     * the Same List of objects. This helps in removing duplicate {@link ParameteredUrl} objects.
     * For the case where different parent nodes extras are sending the same extra value to a single
     * URL.  This prevents the generation of multiple duplicate parametered URLS.
     * See {@link List#equals(Object)} for reference on list equality and
     * {@linkplain UrlParameter#equals(Object)} for UrlParameter equality
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        boolean ret = false;
        if (obj instanceof ParameteredUrl) {
            if (((ParameteredUrl) obj).urlParameterList.equals(this.urlParameterList))
                ret = true;
        }
        return ret;
    }

    /**
     * Extract the candidate parts from a {@linkplain ParameteredUrl} object
     * @param parameteredUrl ParameteredUrl object containing the list of {@linkplain ParameteredUrl.UrlParameter}
     *                       objects.
     * @param idUrlCandidate The {@linkplain UrlCandidate} to which the list of {@linkplain UrlCandidateParts} correspond
     *                       to
     * @return {@code List<UrlCandidateParts>}
     */
    public static List<UrlCandidateParts> toUrlCandidateParts(ParameteredUrl parameteredUrl, Long idUrlCandidate) {
        List<UrlCandidateParts> candidateParts = new LinkedList<>();

        for (UrlParameter parameter : parameteredUrl.urlParameterList) {
            candidateParts.add(new UrlCandidateParts(
                    idUrlCandidate,
                    parameter.order,
                    getIdFromTypes(parameter.type),
                    parameter.urlPiece));
        }

        return candidateParts;
    }


    /**
     * Represents an URL Piece, and all the operations necessary to compare different pieces.
     */
    public class UrlParameter implements Comparable<UrlParameter>{
        public int order;
        public String urlPiece; // Represents an extra's key not its value

        public TYPES type;

        public UrlParameter(int order, TYPES type, String urlPiece) {
            this.order = order;
            this.urlPiece = urlPiece;
            this.type = type;
        }

        @Override
        public int compareTo(@NonNull UrlParameter parameter) {
            return parameter.order >= order ? -1 : 1;
        }

        /**
         * Overrides equality in for UrlParameter where u1 == u2 IFF types, order, and URL
         * Piece are the same
         * @param obj The object to which this instance will be compared against
         * @return
         */
        @Override
        public boolean equals(Object obj) {
            return (obj instanceof UrlParameter) &&
                    ((UrlParameter) obj).type == this.type &&
                    ((UrlParameter) obj).order == this.order &&
                    ((UrlParameter) obj).urlPiece.compareTo(this.urlPiece) == 0;
        }

    }
}

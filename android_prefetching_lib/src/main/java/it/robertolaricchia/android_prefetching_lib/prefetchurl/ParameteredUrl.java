package it.robertolaricchia.android_prefetching_lib.prefetchurl;

import android.support.annotation.NonNull;

import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.robertolaricchia.android_prefetching_lib.room.data.UrlCandidate;
import it.robertolaricchia.android_prefetching_lib.room.data.UrlCandidateParts;

public class ParameteredUrl {

    private List<UrlParameter> urlParameterList = new LinkedList<>();

    public enum TYPES {
        STATIC, PARAMETER
    }

    public static TYPES getTYPESFromId(int id) {
        if (id==0)
            return TYPES.STATIC;
        return TYPES.PARAMETER;
    }

    public static int getIdFromTypes(TYPES types) {
        if (types == TYPES.STATIC)
            return 0;
        return 1;
    }

    public ParameteredUrl() {
    }

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

    @Override
    public boolean equals(Object obj) {
        boolean ret = false;
        if (obj instanceof ParameteredUrl) {
            if (((ParameteredUrl) obj).urlParameterList.equals(this.urlParameterList))
                ret = true;
        }
        return ret;
    }

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

    public class UrlParameter implements Comparable<UrlParameter>{
        public int order;
        public String urlPiece;

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
        @Override
        public boolean equals(Object obj) {
            return (obj instanceof UrlParameter) &&
                    ((UrlParameter) obj).type == this.type &&
                    ((UrlParameter) obj).order == this.order &&
                    ((UrlParameter) obj).urlPiece.compareTo(this.urlPiece) == 0;
        }

    }
}

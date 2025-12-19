package snippet_splitter_out.ds_3;
public class ds_3_snip_55_correctTimeZone {
// Added to allow compilation
// Snippet s87
// SNIPPET_STARTS
private static Date correctTimeZone(final Date date) {
    Date ret = date;
    if (java.util.TimeZone.getDefault().useDaylightTime()) {
        if (java.util.TimeZone.getDefault().inDaylightTime(date))
            ret.setTime(date.getTime() + 1 * 60 * 60 * 1000);
    }
    return ret;
}
}
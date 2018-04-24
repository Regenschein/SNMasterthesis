package model;

import java.util.Objects;

public class Triple {

    private String subject;
    private String predicate;
    private String object;

    public Triple(String subject, String predicate, String object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }


    public String getSubject() {
        return subject;
    }

    public String getPredicate() {
        return predicate;
    }

    public String getObject() {
        return object;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Triple triple = (Triple) o;
        return Objects.equals(subject, triple.subject) &&
                Objects.equals(predicate, triple.predicate) &&
                Objects.equals(object, triple.object);
    }

    @Override
    public String toString() {
        return "Triple{" +
                "subject='" + subject + '\'' +
                ", predicate='" + predicate + '\'' +
                ", object='" + object + '\'' +
                '}';
    }

    public String toTSV() {
        return(subject + "\t" + predicate + "\t" + object + "\n");
    }

    public String toN3() {
        Model model = Model.getInstance();
        String prefixSub = subject.split(":")[0];
        prefixSub = model.getPrefix(prefixSub);
        if (predicate.contains(":")){
            String prefixPred = predicate.split(":")[0];
            prefixPred = model.getPrefix(prefixPred);
            predicate = (prefixPred + predicate.split(":")[1] + ">");
        } else {
            //String prefixPred = "<https:test.uri/";
            String prefixPred = "";
            //predicate = (prefixPred + predicate + ">");
            //predicate = (prefixPred + predicate + ">");
        }
        if (!object.startsWith("\"")){
            String prefixObj = object.split(":")[0];
            prefixObj = model.getPrefix(prefixObj);
            try{
                object = (prefixObj + object.split(":")[1] + ">");
            } catch (Exception e){

            }

        } else if (object.contains(":")){
            String literal = object.split("\\^\\^")[0];
            String prefixObj[] = object.split("\\^\\^")[1].split(":");
            String prefixObje = model.getPrefix(prefixObj[0]);
            object = literal + "^^" + prefixObje + prefixObj[1] + ">";
            System.out.println("OBJECT: " + object);
        }
        System.out.println(prefixSub + subject.split(":")[1] + "> " + predicate + " " + object);
        return (prefixSub + subject.split(":")[1] + "> " + predicate + " " + object + ".\n");
    }


    @Override
    public int hashCode() {

        return Objects.hash(subject, predicate, object);
    }
}

package com.sbt.dictionary.check;

import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CycleChecker {

    private final XmlModel model;

    private final Set<String> noCycleDictionaries = new HashSet<>();
    private final Set<String> cycleDictionaries = new HashSet<>();

    public static CycleChecker of(XmlModel model) {
        return new CycleChecker(model);
    }

    private CycleChecker(XmlModel model) {
        this.model = model;
        analyzeDictionaries();
    }

    private void analyzeDictionaries() {
        final List<XmlModelClass> dictClasses = model.getClassesAsList().stream()
                .filter(it -> it.isDictionary() && !it.isAbstract())
                .collect(Collectors.toList());

        final List<XmlModelClass> cycleClasses = dictClasses.stream()
                .filter(it -> isCycle(it, new ArrayList<>()).isEmpty())
                .collect(Collectors.toList());
        noCycleDictionaries.addAll(
                cycleClasses.stream()
                        .map(XmlModelClass::getName)
                        .collect(Collectors.toSet())
        );

        cycleDictionaries.addAll(
                dictClasses.stream()
                        .map(XmlModelClass::getName)
                        .filter(name -> !noCycleDictionaries.contains(name))
                        .collect(Collectors.toSet())
        );

    }

    public Set<String> getCycleDictionaries() {
        return new HashSet<>(cycleDictionaries);
    }

    public Set<String> getNoCycleDictionaries() {
        return new HashSet<>(noCycleDictionaries);
    }

    public List<String> isCycle(XmlModelClass modelClass, List<String> chain) {
        List<String> localChain = new ArrayList<>(chain);
        localChain.add(modelClass.getName());

        if (chain.contains(modelClass.getName())) {
            return localChain;
        }

        final List<XmlModelClass> dictionaryPropertyClasses = modelClass.getPropertiesWithIncome().stream()
                .filter(property -> {
                    if (property.getMappedBy() != null) {
                        return false;
                    }
                    final XmlModelClass xmlModelClass = model.getClassNullable(property.getType());
                    if (xmlModelClass == null) {
                        return false;
                    }
                    return xmlModelClass.isDictionary();
                })
                .map(property -> model.getClass(property.getType()))
                .collect(Collectors.toList());


        for (XmlModelClass dictClass : dictionaryPropertyClasses) {
            final List<String> cycleChain = isCycle(dictClass, localChain);
            if (!cycleChain.isEmpty()) {
                return cycleChain;
            }
        }

        return Collections.emptyList();
    }
}

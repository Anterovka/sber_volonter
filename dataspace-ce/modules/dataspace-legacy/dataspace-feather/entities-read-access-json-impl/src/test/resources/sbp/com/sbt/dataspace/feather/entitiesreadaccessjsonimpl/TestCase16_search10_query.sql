select 0, t0.ID c2, t0.SERVICE c3 from F_OPERATION t0 left join F_ENTITY t1 on t0.ID = t1.ID where t0.ID = :p0::varchar and t1.NAME like :p1::varchar
